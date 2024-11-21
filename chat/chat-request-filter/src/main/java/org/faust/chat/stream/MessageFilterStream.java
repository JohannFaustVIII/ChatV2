package org.faust.chat.stream;

import org.faust.chat.Message;
import org.faust.chat.command.AddMessage;
import org.faust.chat.command.DeleteMessage;
import org.faust.chat.command.EditMessage;
import org.faust.chat.external.ChannelService;
import org.faust.chat.external.ChatService;
import org.faust.chat.external.KeycloakService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class MessageFilterStream {
    public final static String INPUT_TOPIC = "CHAT_REQUEST";
    public final static String OUTPUT_TOPIC = "CHAT_COMMAND";

    private final ChannelService channelService;
    private final ChatService chatService;
    private final KeycloakService keycloakService;

    public MessageFilterStream(ChannelService channelService, ChatService chatService, KeycloakService keycloakService) {
        this.channelService = channelService;
        this.chatService = chatService;
        this.keycloakService = keycloakService;
    }

    @Bean
    public KStream<String, Object> requestFilter(StreamsBuilder streamsBuilder) {
        KStream<String, Object> input = streamsBuilder.stream(INPUT_TOPIC);
        KStream<String, Object> output = input
                .filter((key, value) -> switch(value) {
                        case AddMessage add -> filter(add);
                        case DeleteMessage delete -> filter(delete);
                        case EditMessage edit -> filter(edit);
                        default -> false; //TODO: implement
                    }
                );

        output.to(OUTPUT_TOPIC);
        return output;
    }

    public boolean filter(AddMessage command) {
        if (!keycloakService.existsUser(command.senderId())) {
            // TODO: USER UNKNOWN EXCEPTION to...
            return false;
        }

        if (!channelService.existsChannel(command.channel())) {
            // TODO: CHANNEL UNKNOWN EXCEPTION to...
            return false;
        }

        return true;
    }

    public boolean filter(DeleteMessage command) {
        if (!channelService.existsChannel(command.channel())) {
            // TODO: CHANNEL UNKNOWN EXCEPTION to...
            return false;
        }

        if (!keycloakService.existsUser(command.userId())) {
            // TODO: USER UNKNOWN EXCEPTION to...
            return false;
        }

        Message oldMessage = chatService.getMessage(command.messageId());
        if (oldMessage == null) {
            // TODO: MESSAGE UNKNOWN EXCEPTION to...
            return false;
        }
        if (!oldMessage.channelId().equals(command.messageId())) {
            // TODO: MESSAGE UNKNOWN EXCEPTION to...
            return false;
        }
        if (!oldMessage.senderId().equals(command.messageId())) {
            // TODO: INVALID PERMISSION EXCEPTION to...
            return false;
        }

        return true;
    }

    public boolean filter(EditMessage command) {
        if (!channelService.existsChannel(command.channel())) {
            // TODO: CHANNEL UNKNOWN EXCEPTION to...
            return false;
        }

        if (!keycloakService.existsUser(command.userId())) {
            // TODO: USER UNKNOWN EXCEPTION to...
            return false;
        }

        Message oldMessage = chatService.getMessage(command.messageId());
        if (oldMessage == null) {
            // TODO: MESSAGE UNKNOWN EXCEPTION to...
            return false;
        }
        if (!oldMessage.channelId().equals(command.channel())) {
            // TODO: MESSAGE UNKNOWN EXCEPTION to...
            return false;
        }
        if (!oldMessage.senderId().equals(command.userId())) {
            // TODO: INVALID PERMISSION EXCEPTION to...
            return false;
        }

        return true;
    }


}
