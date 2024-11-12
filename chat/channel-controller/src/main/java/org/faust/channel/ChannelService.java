package org.faust.channel;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChannelService {
    private static final String ADD_CHANNEL_TOPIC_NAME = "ADD_CHANNEL";

    private final KafkaTemplate<String, Channel> kafkaTemplate;

    public ChannelService(KafkaTemplate<String, Channel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void addChannel(String name) {
        Channel channel = new Channel(UUID.randomUUID(), name);
        kafkaTemplate.send(ADD_CHANNEL_TOPIC_NAME, channel.id().toString(), channel);
    }
}
