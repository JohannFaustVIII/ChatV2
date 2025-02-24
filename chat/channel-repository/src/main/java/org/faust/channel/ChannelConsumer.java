package org.faust.channel;

import org.faust.channel.command.AddChannel;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChannelConsumer {
    private static final String ADD_CHANNEL_TOPIC_NAME = "ADD_CHANNEL";
    public final static String SSE_TOPIC = "SSE_EVENTS";

    private final ChannelRepository channelRepository;
    private final KafkaTemplate<String, org.faust.sse.Message> kafkaTemplate;

    public ChannelConsumer(ChannelRepository channelRepository, KafkaTemplate<String, org.faust.sse.Message> kafkaTemplate) {
        this.channelRepository = channelRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = ADD_CHANNEL_TOPIC_NAME)
    public void addChannel(AddChannel command) {
        if (channelRepository.existsChannelWithName(command.channel().name())) {
            kafkaTemplate.send(SSE_TOPIC, command.tokenId().toString(),
                    org.faust.sse.Message.error(command.tokenId(), "Exists channel with given name.")
            );
            return;
        }
        channelRepository.addChannel(command.channel());
    }

}
