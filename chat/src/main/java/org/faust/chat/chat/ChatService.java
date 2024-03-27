package org.faust.chat.chat;

import org.faust.chat.channel.ChannelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;

    public ChatService(MessageRepository messageRepository, ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
    }

    public void addMessage(Message message) {
        messageRepository.addMessage(message);
    }

    public List<Message> getMessages(UUID channel) {
        if (!channelRepository.isChannelExist(channel)) {
            throw new ChannelUnknownException();
        }
        return messageRepository.getAllMessages(channel);
    }

    public void addMessage(UUID channel, String message) {
        if (!channelRepository.isChannelExist(channel)) {
            throw new ChannelUnknownException();
        }
        messageRepository.addMessage(new Message(
                UUID.randomUUID(),
                channel,
                message,
                LocalDateTime.now()
        ));
    }

    private final static class ChannelUnknownException extends RuntimeException {}
}
