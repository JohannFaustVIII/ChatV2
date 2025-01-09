package org.faust.channel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// TODO: Instead adding to repository, check sending to Kafka

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    KafkaTemplate<String, Channel>  kafkaTemplate;

    @InjectMocks
    ChannelService channelService;

    @Captor
    ArgumentCaptor<Channel> argumentCaptor;

    @Test
    public void whenAddingChannelThenCommandSendToKafka() {
        // given
        String channelToAdd = "Random name";

        // when
        channelService.addChannel(channelToAdd);

        //then
        verify(kafkaTemplate).send(eq("ADD_CHANNEL"), any(String.class), argumentCaptor.capture());
        Channel channelSentToAdd = argumentCaptor.getValue();
        Assertions.assertEquals(channelToAdd, channelSentToAdd.name());
    }
}