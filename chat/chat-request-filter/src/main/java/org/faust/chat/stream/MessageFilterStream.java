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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class MessageFilterStream {
    public final static String INPUT_TOPIC = "CHAT_REQUEST";
    public final static String OUTPUT_TOPIC = "CHAT_COMMAND";
    public final static String SSE_TOPIC = "SSE_EVENTS";

    private final ChannelService channelService;
    private final ChatService chatService;
    private final KeycloakService keycloakService;
    private final KafkaTemplate<String, org.faust.sse.Message> kafkaTemplate;

    public MessageFilterStream(ChannelService channelService, ChatService chatService, KeycloakService keycloakService, KafkaTemplate<String, org.faust.sse.Message> kafkaTemplate) {
        this.channelService = channelService;
        this.chatService = chatService;
        this.keycloakService = keycloakService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public KStream<String, Object> requestFilter(StreamsBuilder streamsBuilder) {
        KStream<String, Object> input = streamsBuilder.stream(INPUT_TOPIC);
        KStream<String, Object> output = input
                .filter((key, value) -> switch(value) {
                        case AddMessage add -> filter(add);
                        case DeleteMessage delete -> filter(delete);
                        case EditMessage edit -> filter(edit);
                        default -> false; //TODO: would be nice to store somewhere info about this case, as requester Id is not reachable, should be sent to a log store? need a log store
                    }
                );

        output.to(OUTPUT_TOPIC);
        return output;
    }

    public boolean filter(AddMessage command) {
        if (!keycloakService.existsUser(command.senderId())) {
            kafkaTemplate.send(SSE_TOPIC, command.requesterId().toString(),
                    org.faust.sse.Message.error(command.requesterId(), "Requested user not found.")
            );
            return false;
        }

        if (!channelService.existsChannel(command.channel())) {
            kafkaTemplate.send(SSE_TOPIC, command.requesterId().toString(),
                    org.faust.sse.Message.error(command.requesterId(), "Requested channel not found.")
            );
            return false;
        }

        return true;
    }

    public boolean filter(DeleteMessage command) {
        if (!channelService.existsChannel(command.channel())) {
            kafkaTemplate.send(SSE_TOPIC, command.requesterId().toString(),
                    org.faust.sse.Message.error(command.requesterId(), "Requested channel not found.")
            );
            return false;
        }

        if (!keycloakService.existsUser(command.userId())) {
            kafkaTemplate.send(SSE_TOPIC, command.requesterId().toString(),
                    org.faust.sse.Message.error(command.requesterId(), "Requested user not found.")
            );
            return false;
        }

        Message oldMessage = chatService.getMessage(command.messageId());
        if (oldMessage == null) {
            kafkaTemplate.send(SSE_TOPIC, command.requesterId().toString(),
                    org.faust.sse.Message.error(command.requesterId(), "Requested message not found.")
            );
            return false;
        }
        if (!oldMessage.channelId().equals(command.messageId())) {
            kafkaTemplate.send(SSE_TOPIC, command.requesterId().toString(),
                    org.faust.sse.Message.error(command.requesterId(), "Requested message not found.")
            );
            return false;
        }
        if (!oldMessage.senderId().equals(command.messageId())) {
            kafkaTemplate.send(SSE_TOPIC, command.requesterId().toString(),
                    org.faust.sse.Message.error(command.requesterId(), "Invalid permissions to perform requested action.")
            );
            return false;
        }

        return true;
    }

    public boolean filter(EditMessage command) {
        if (!channelService.existsChannel(command.channel())) {
            kafkaTemplate.send(SSE_TOPIC, command.requesterId().toString(),
                    org.faust.sse.Message.error(command.requesterId(), "Requested channel not found.")
            );
            return false;
        }

        if (!keycloakService.existsUser(command.userId())) {
            kafkaTemplate.send(SSE_TOPIC, command.requesterId().toString(),
                    org.faust.sse.Message.error(command.requesterId(), "Requested user not found.")
            );
            return false;
        }

        Message oldMessage = chatService.getMessage(command.messageId());
        if (oldMessage == null) {
            kafkaTemplate.send(SSE_TOPIC, command.requesterId().toString(),
                    org.faust.sse.Message.error(command.requesterId(), "Requested message not found.")
            );
            return false;
        }
        if (!oldMessage.channelId().equals(command.channel())) {
            kafkaTemplate.send(SSE_TOPIC, command.requesterId().toString(),
                    org.faust.sse.Message.error(command.requesterId(), "Requested message not found.")
            );
            return false;
        }
        if (!oldMessage.senderId().equals(command.userId())) {
            kafkaTemplate.send(SSE_TOPIC, command.requesterId().toString(),
                    org.faust.sse.Message.error(command.requesterId(), "Invalid permissions to perform requested action.")
            );
            return false;
        }

        return true;
    }


}
