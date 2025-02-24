package org.faust.chat;

import org.faust.chat.command.AddMessage;
import org.faust.chat.command.DeleteMessage;
import org.faust.chat.command.EditMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ChatService {
    private static final String DML_CHAT_TOPIC_NAME = "CHAT_REQUEST";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ChatService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void addMessage(UUID channel, String sender, UUID senderId, UUID tokenId, String message) {
        kafkaTemplate.send(DML_CHAT_TOPIC_NAME, channel.toString(), new AddMessage(tokenId, channel, sender, senderId, message, LocalDateTime.now()));
    }

    public void editMessage(UUID channel, UUID messageId, UUID userId, UUID tokenId, String newMessage) {
        kafkaTemplate.send(DML_CHAT_TOPIC_NAME, channel.toString(), new EditMessage(tokenId, channel, messageId, userId, newMessage, LocalDateTime.now()));
    }

    public void deleteMessage(UUID channel, UUID messageId,UUID userId, UUID tokenId) {
        kafkaTemplate.send(DML_CHAT_TOPIC_NAME, channel.toString(), new DeleteMessage(tokenId, channel, messageId, userId));
    }

}
