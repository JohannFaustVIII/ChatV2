package org.faust.chat.chat;

import org.faust.chat.channel.ChannelService;
import org.faust.chat.exception.ChannelUnknownException;
import org.faust.chat.exception.MessageUnknownException;
import org.faust.chat.exception.InvalidPermissionsException;
import org.faust.chat.exception.UserUnknownException;
import org.faust.chat.keycloak.KeycloakService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Service
public class ChatService {

    private final MessageRepository messageRepository;
    private final ChannelService channelService;
    private final KeycloakService keycloakService;

    public ChatService(MessageRepository messageRepository, ChannelService channelService, KeycloakService keycloakService) {
        this.messageRepository = messageRepository;
        this.channelService = channelService;
        this.keycloakService = keycloakService;
    }

    public Collection<Message> getMessages(UUID channel) {
        return getMessages(channel, null, null, 10);
    }

    public Collection<Message> getMessages(UUID channel, UUID before, UUID after, int limit) {
        if (!channelService.existsChannel(channel)) {
            throw new ChannelUnknownException();
        }
        return messageRepository.getAllMessages(channel, before, after, limit);
    }

    public String addMessage(UUID channel, String sender, UUID senderId, String message) {
        if (!keycloakService.existsUser(senderId)) {
            throw new UserUnknownException();
        }

        if (!channelService.existsChannel(channel)) {
            throw new ChannelUnknownException();
        }
        messageRepository.addMessage(new Message(
                UUID.randomUUID(),
                channel,
                sender,
                message,
                LocalDateTime.now(),
                null,
                senderId
        ));
        return channel.toString();
    }

    public void editMessage(UUID channel, UUID messageId, String user, String newMessage) {
        Message oldMessage = messageRepository.getMessage(messageId);
        if (oldMessage == null) {
            throw new MessageUnknownException();
        }
        if (!oldMessage.channelId().equals(channel)) {
            throw new MessageUnknownException();
        }
        if (!oldMessage.sender().equals(user)) { // it should use UUID instead user's name
            throw new InvalidPermissionsException();
        }

        messageRepository.editMessage(messageId, newMessage);
    }

    public void deleteMessage(UUID channel, UUID messageId, String user) {
        Message oldMessage = messageRepository.getMessage(messageId);
        if (oldMessage == null) {
            throw new MessageUnknownException();
        }
        if (!oldMessage.channelId().equals(channel)) {
            throw new MessageUnknownException();
        }
        if (!oldMessage.sender().equals(user)) { // here, user with admin access should be able to remove too, also should use UUID instead user's name
            throw new InvalidPermissionsException();
        }

        messageRepository.deleteMessage(messageId);
    }

}
