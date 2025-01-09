package org.faust.channel;

import org.faust.sse.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChannelConsumerTest {

    @Mock
    ChannelRepository channelRepository;

    @Mock
    KafkaTemplate<String, Message> kafkaTemplate;

    @InjectMocks
    ChannelConsumer testedConsumer;

    @Test
    public void whenAddChannelThenSuccess() {
        //given
        UUID channelId = UUID.randomUUID();
        String channelName = "Random Channel Name";
        Channel channelToAdd = new Channel(channelId, channelName);
        when(channelRepository.existsChannelWithName(channelName)).thenReturn(false);

        //when
        testedConsumer.addChannel(channelToAdd);

        // then
        verify(channelRepository).addChannel(eq(channelToAdd));
    }

    @Test
    public void whenAddChannelWithExistingNameThenException() {
        //given
        UUID channelId = UUID.randomUUID();
        String channelName = "Random Channel Name";
        Channel channelToAdd = new Channel(channelId, channelName);
        when(channelRepository.existsChannelWithName(channelName)).thenReturn(true);

        //when
        testedConsumer.addChannel(channelToAdd);

        // then
        verify(channelRepository, never()).addChannel(eq(channelToAdd));
    }
}
