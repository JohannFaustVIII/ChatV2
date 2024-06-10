package org.faust.chat.chat;

import org.faust.chat.channel.ChannelRepository;
import org.faust.chat.channel.ChannelService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final MessageRepository messageRepository;
    private final ChannelService channelService;

    public ChatService(MessageRepository messageRepository, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.channelService = channelService;
    }

    public List<Message> getMessages(UUID channel, UUID before, UUID after, int limit) {
        if (!channelService.existsChannel(channel)) {
            throw new ChannelUnknownException();
        }
        return messageRepository.getAllMessages(channel, before, after, limit);
    }

    public String addMessage(UUID channel, String sender, String message) {
        if (!channelService.existsChannel(channel)) {
            throw new ChannelUnknownException();
        }
        messageRepository.addMessage(new Message(
                UUID.randomUUID(),
                channel,
                sender,
                message,
                LocalDateTime.now()
        ));
        return channel.toString();
    }

    private final static class ChannelUnknownException extends RuntimeException {}
}
