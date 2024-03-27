package org.faust.chat;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ChannelRepository {

    private final List<Channel> channels;

    public ChannelRepository() {
        this.channels = new ArrayList<>();
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public List<Channel> getAllChannels() {
        return channels;
    }
}
