package org.faust.chat.channel;

import org.faust.chat.sse.SSEService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final SSEService sseService;

    public ChannelService(ChannelRepository channelRepository, SSEService sseService) {
        this.channelRepository = channelRepository;
        this.sseService = sseService;
    }

    public void addChannel(String name) {
        channelRepository.addChannel(new Channel(
                UUID.randomUUID(),
                name
        ));
        sseService.emitEvents("channel"); // TODO: this can be handled in aspect?
    }

    public List<Channel> getAllChannels() {
        return channelRepository.getAllChannels();
    }

}
