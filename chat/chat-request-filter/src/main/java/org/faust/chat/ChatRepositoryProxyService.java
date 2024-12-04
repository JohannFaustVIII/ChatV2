package org.faust.chat;

import org.faust.chat.exception.ChannelUnknownException;
import org.faust.chat.external.ChannelService;
import org.faust.chat.external.ChatService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class ChatRepositoryProxyService {

    private final ChannelService channelService;

    private final ChatService chatService;

    public ChatRepositoryProxyService(ChannelService channelService, ChatService chatService) {
        this.channelService = channelService;
        this.chatService = chatService;
    }

    public Collection<Message> getAllMessages(UUID channel, UUID before, UUID after, int limit) {
        if (!channelService.existsChannel(channel)) {
            throw new ChannelUnknownException();
        }
        return chatService.getMessages(channel, before, after, limit);
    }
}
