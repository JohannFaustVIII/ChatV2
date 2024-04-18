package org.faust.chat.chat;

import org.faust.chat.channel.ChannelRepository;
import org.faust.chat.sse.SSEService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;

    private final SSEService sseService;

    public ChatService(MessageRepository messageRepository, ChannelRepository channelRepository, SSEService sseService) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.sseService = sseService;
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
        sseService.emitEvents(channel.toString());
    }

    private final static class ChannelUnknownException extends RuntimeException {}
}
