package org.faust.chat.channel;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ChannelRepository {

    private final List<Channel> channels;

    public ChannelRepository() {
        this.channels = new ArrayList<>();
        // TODO: remove it, only for testing
        this.channels.add(new Channel(UUID.randomUUID(), "RandomChannel1"));
        this.channels.add(new Channel(UUID.randomUUID(), "RandomChannel2"));
        this.channels.add(new Channel(UUID.randomUUID(), "RC3"));
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public List<Channel> getAllChannels() {
        return channels;
    }

    public boolean isChannelExist(UUID channel) {
        return channels.stream().map(Channel::id).anyMatch(c -> c.equals(channel));
    }
}
