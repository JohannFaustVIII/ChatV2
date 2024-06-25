package org.faust.chat.channel;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;

    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public void addChannel(String name) {
        channelRepository.addChannel(new Channel(
                UUID.randomUUID(),
                name
        ));
    }

    public Collection<Channel> getAllChannels() {
        return channelRepository.getAllChannels();
    }

    public boolean existsChannel(UUID channelUUID) {
        return channelRepository.existsChannel(channelUUID);
    }

}
