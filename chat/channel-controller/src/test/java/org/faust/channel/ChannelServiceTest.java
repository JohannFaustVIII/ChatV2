package org.faust.channel;

import org.faust.channel.command.AddChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    KafkaTemplate<String, AddChannel>  kafkaTemplate;

    @InjectMocks
    ChannelService channelService;

    @Captor
    ArgumentCaptor<AddChannel> argumentCaptor;

    @Test
    public void whenAddingChannelThenCommandSendToKafka() {
        // given
        String channelToAdd = "Random name";
        UUID tokenId = UUID.randomUUID();

        // when
        channelService.addChannel(tokenId, channelToAdd);

        //then
        verify(kafkaTemplate).send(eq("ADD_CHANNEL"), any(String.class), argumentCaptor.capture());
        AddChannel channelSentToAdd = argumentCaptor.getValue();
        Assertions.assertEquals(channelToAdd, channelSentToAdd.channel().name());
        Assertions.assertEquals(tokenId, channelSentToAdd.tokenId());
    }
}