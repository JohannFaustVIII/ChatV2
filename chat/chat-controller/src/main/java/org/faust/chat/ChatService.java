package org.faust.chat;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Component
public class ChatService {
    private static final String ADD_MESSAGE_TOPIC_NAME = "ADD_MESSAGE";

    private final KafkaTemplate<String, ?> kafkaTemplate;

    public ChatService(KafkaTemplate<String, ?> kafkaTemplate) {
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

    public String addMessage(UUID channel, String sender, UUID senderId, String message) {
//        if (!keycloakService.existsUser(senderId)) {
//            throw new UserUnknownException();
//        }
//
//        if (!channelService.existsChannel(channel)) {
//            throw new ChannelUnknownException();
//        }
//        messageRepository.addMessage(new Message(
//                UUID.randomUUID(),
//                channel,
//                sender,
//                message,
//                LocalDateTime.now(),
//                null,
//                senderId
//        ));
//        return channel.toString();
        return null; //TODO: maybe, group these checks and move to different streams?
    }

    public void editMessage(UUID channel, UUID messageId, UUID userId, String newMessage) {
//        if (!channelService.existsChannel(channel)) {
//            throw new ChannelUnknownException();
//        }
//
//        if (!keycloakService.existsUser(userId)) {
//            throw new UserUnknownException();
//        }
//
//        Message oldMessage = messageRepository.getMessage(messageId);
//        if (oldMessage == null) {
//            throw new MessageUnknownException();
//        }
//        if (!oldMessage.channelId().equals(channel)) {
//            throw new MessageUnknownException();
//        }
//        if (!oldMessage.senderId().equals(userId)) {
//            throw new InvalidPermissionsException();
//        }
//
//        messageRepository.editMessage(messageId, newMessage);
        //TODO: maybe, group these checks and move to different streams?
    }

    public void deleteMessage(UUID channel, UUID messageId,UUID userId) {
//        if (!channelService.existsChannel(channel)) {
//            throw new ChannelUnknownException();
//        }
//
//        if (!keycloakService.existsUser(userId)) {
//            throw new UserUnknownException();
//        }
//
//        Message oldMessage = messageRepository.getMessage(messageId);
//        if (oldMessage == null) {
//            throw new MessageUnknownException();
//        }
//        if (!oldMessage.channelId().equals(channel)) {
//            throw new MessageUnknownException();
//        }
//        if (!oldMessage.senderId().equals(userId)) {
//            throw new InvalidPermissionsException();
//        }
//
//        messageRepository.deleteMessage(messageId);
         //TODO: maybe, group these checks and move to different streams?
    }

}
