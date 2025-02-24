package org.faust.chat;

import org.faust.chat.command.AddMessage;
import org.faust.chat.command.DeleteMessage;
import org.faust.chat.command.EditMessage;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@KafkaListener(topics = "CHAT_COMMAND")
public class MessageConsumer {

    public final static String SSE_TOPIC = "SSE_EVENTS";
    private final MessageRepository messageRepository;
    private final KafkaTemplate<String, org.faust.sse.Message> kafkaTemplate;

    public MessageConsumer(MessageRepository messageRepository, KafkaTemplate<String, org.faust.sse.Message> kafkaTemplate) {
        this.messageRepository = messageRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaHandler
    public void addMessage(AddMessage command) {
        messageRepository.addMessage(new Message(
                UUID.randomUUID(),
                command.channel(),
                command.sender(),
                command.message(),
                command.sendTime(),
                null,
                command.senderId()
        ));
        kafkaTemplate.send(SSE_TOPIC, command.channel().toString(), org.faust.sse.Message.globalNotify(command.channel().toString()));
    }

    @KafkaHandler
    public void editMessage(EditMessage command) {
        Message oldMessage = messageRepository.getMessage(command.messageId());
        if (oldMessage == null) {
            kafkaTemplate.send(SSE_TOPIC, command.tokenId().toString(),
                    org.faust.sse.Message.error(command.tokenId(), "Requested message to edit is unknown.")
            );
            return;
        }
        if (!oldMessage.channelId().equals(command.channel())) {
            kafkaTemplate.send(SSE_TOPIC, command.tokenId().toString(),
                    org.faust.sse.Message.error(command.tokenId(), "Requested message to edit is unknown.")
            );
            return;
        }
        if (!oldMessage.senderId().equals(command.userId())) {
            kafkaTemplate.send(SSE_TOPIC, command.tokenId().toString(),
                    org.faust.sse.Message.error(command.tokenId(), "Invalid permissions to edit the message.")
            );
            return;
        }
        messageRepository.editMessage(command.messageId(), command.newMessage(), command.editTime());
        kafkaTemplate.send(SSE_TOPIC, command.channel().toString(), org.faust.sse.Message.globalNotify(command.channel().toString()));
    }

    @KafkaHandler
    public void deleteMessage(DeleteMessage command) {
        Message oldMessage = messageRepository.getMessage(command.messageId());
        if (oldMessage == null) {
            kafkaTemplate.send(SSE_TOPIC, command.tokenId().toString(),
                    org.faust.sse.Message.error(command.tokenId(), "Requested message to delete is unknown.")
            );
            return;
        }
        if (!oldMessage.channelId().equals(command.channel())) {
            kafkaTemplate.send(SSE_TOPIC, command.tokenId().toString(),
                    org.faust.sse.Message.error(command.tokenId(), "Requested message to delete is unknown.")
            );
            return;
        }
        if (!oldMessage.senderId().equals(command.userId())) {
            kafkaTemplate.send(SSE_TOPIC, command.tokenId().toString(),
                    org.faust.sse.Message.error(command.tokenId(), "Invalid permissions to remove the message.")
            );
            return;
        }

        messageRepository.deleteMessage(command.messageId());
        kafkaTemplate.send(SSE_TOPIC, command.channel().toString(), org.faust.sse.Message.globalNotify(command.channel().toString()));
    }

}
