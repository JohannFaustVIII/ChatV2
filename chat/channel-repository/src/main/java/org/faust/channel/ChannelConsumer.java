package org.faust.channel;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ChannelConsumer {
    private static final String ADD_CHANNEL_TOPIC_NAME = "ADD_CHANNEL";

    private final ChannelRepository channelRepository;

    public ChannelConsumer(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @KafkaListener(topics = ADD_CHANNEL_TOPIC_NAME)
    public void addChannel(Channel channel) {
        channelRepository.addChannel(channel);
    }

}
