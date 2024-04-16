package org.faust.chat.channel;

import org.faust.chat.sse.SSEService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final SSEService service;

    public ChannelService(ChannelRepository channelRepository, SSEService service) {
        this.channelRepository = channelRepository;
        this.service = service;
    }

    public void addChannel(String name) {
        channelRepository.addChannel(new Channel(
                UUID.randomUUID(),
                name
        ));
    }

    public List<Channel> getAllChannels(){
        service.emitEvents("Read channels " + LocalDateTime.now());
        return channelRepository.getAllChannels();
    }

}
