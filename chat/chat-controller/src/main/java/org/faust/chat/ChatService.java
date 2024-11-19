package org.faust.chat;

import org.faust.chat.command.AddMessage;
import org.faust.chat.command.DeleteMessage;
import org.faust.chat.command.EditMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Component
public class ChatService {
    private static final String DML_CHAT_TOPIC_NAME = "DML_CHAT";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ChatService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public Collection<Message> getMessages(UUID channel) {
        return getMessages(channel, null, null, 10);
    }

    public Collection<Message> getMessages(UUID channel, UUID before, UUID after, int limit) {
//        if (!channelService.existsChannel(channel)) {
//            throw new ChannelUnknownException();
//        }
//        return messageRepository.getAllMessages(channel, before, after, limit);
        return null; //TODO: move to repository?
    }

    public void addMessage(UUID channel, String sender, UUID senderId, String message) {
        kafkaTemplate.send(DML_CHAT_TOPIC_NAME, new AddMessage(channel, sender, senderId, message));
    }

    public void editMessage(UUID channel, UUID messageId, UUID userId, String newMessage) {
        kafkaTemplate.send(DML_CHAT_TOPIC_NAME, new EditMessage(channel, messageId, userId, newMessage));
    }

    public void deleteMessage(UUID channel, UUID messageId,UUID userId) {
        kafkaTemplate.send(DML_CHAT_TOPIC_NAME, new DeleteMessage(channel, messageId, userId));
    }

}
