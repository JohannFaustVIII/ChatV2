package org.faust.channel;

import org.faust.channel.command.AddChannel;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChannelService {
    private static final String ADD_CHANNEL_TOPIC_NAME = "ADD_CHANNEL";

    private final KafkaTemplate<String, AddChannel> kafkaTemplate;

    public ChannelService(KafkaTemplate<String, AddChannel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void addChannel(UUID tokenId, String name) {
        Channel channel = new Channel(UUID.randomUUID(), name);
        kafkaTemplate.send(ADD_CHANNEL_TOPIC_NAME, channel.id().toString(), new AddChannel(tokenId, channel));
    }
}
