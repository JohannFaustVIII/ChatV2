package org.faust.channel;

//import org.faust.chat.exception.ChannelExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// TODO: Instead adding to repository, check sending to Kafka

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {
//
//    @Mock
//    ChannelRepository channelRepository;
//
//    @Test
//    public void whenAddChannelThenSuccess() {
//        //given
//        String channelToAdd = "Random name";
//
//        List<Channel> addedChannels = new ArrayList<>();
//        Mockito.when(channelRepository.getAllChannels()).thenReturn(addedChannels);
//        Mockito.doAnswer(invocation -> {
//            addedChannels.add(invocation.getArgument(0));
//            return null;
//        }).when(channelRepository).addChannel(ArgumentMatchers.any(Channel.class));
//
//
//        ChannelService testedService = new ChannelService(channelRepository);
//
//        //then before
//        Assertions.assertEquals(testedService.getAllChannels().size(), 0);
//
//        //when
//        testedService.addChannel(channelToAdd);
//
//        //then after
//        Collection<Channel> resultChannels = testedService.getAllChannels();
//        Assertions.assertEquals(resultChannels.size(), 1);
//        Assertions.assertTrue(resultChannels.stream().anyMatch(c -> c.name().equals(channelToAdd)));
//    }
//
//    @Test
//    public void whenAddChannelWithExistingNameThenException() {
//        //given
//        String channelToAdd = "Random name";
//        UUID channelId = UUID.randomUUID();
//
//        List<Channel> addedChannels = new ArrayList<>();
//        addedChannels.add(new Channel(channelId, channelToAdd));
//        Mockito.when(channelRepository.existsChannelWithName(channelToAdd)).thenReturn(true);
//
//        ChannelService testedService = new ChannelService(channelRepository);
//
//        //when-then
//        Assertions.assertThrows(ChannelExistsException.class, () -> testedService.addChannel(channelToAdd));
//    }
//
//    @Test
//    public void whenGetNoChannelsThenEmptyCollection() {
//        //given
//        List<Channel> addedChannels = new ArrayList<>();
//        Mockito.when(channelRepository.getAllChannels()).thenReturn(addedChannels);
//
//        ChannelService testedService = new ChannelService(channelRepository);
//
//        //when
//        Collection<Channel> resultChannels = testedService.getAllChannels();
//
//        //then after
//        Assertions.assertEquals(resultChannels.size(), 0);
//    }
//
//    @Test
//    public void whenGetExistingChannelsThenAllReturned() {
//        //given
//        List<Channel> addedChannels = new ArrayList<>();
//        addedChannels.add(new Channel(UUID.randomUUID(), "c1"));
//        addedChannels.add(new Channel(UUID.randomUUID(), "c2"));
//        Mockito.when(channelRepository.getAllChannels()).thenReturn(addedChannels);
//
//        ChannelService testedService = new ChannelService(channelRepository);
//
//        //when
//        Collection<Channel> resultChannels = testedService.getAllChannels();
//
//        //then after
//        Assertions.assertEquals(resultChannels.size(), 2);
//        Iterator<Channel> resultIterator = resultChannels.iterator();
//        Assertions.assertEquals(resultIterator.next(), addedChannels.get(0));
//        Assertions.assertEquals(resultIterator.next(), addedChannels.get(1));
//    }
//
//    @Test
//    public void whenCheckExistingChannelThenTrue() {
//        //given
//        UUID channelToCheck = UUID.randomUUID();
//        Mockito.when(channelRepository.existsChannelWithId(channelToCheck)).thenReturn(true);
//
//        ChannelService testedService = new ChannelService(channelRepository);
//
//        //when
//        boolean result = testedService.existsChannel(channelToCheck);
//
//        //then after
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    public void whenCheckNotExistingChannelThenFalse() {
//        //given
//        UUID channelToCheck = UUID.randomUUID();
//        Mockito.when(channelRepository.existsChannelWithId(channelToCheck)).thenReturn(false);
//
//        ChannelService testedService = new ChannelService(channelRepository);
//
//        //when
//        boolean result = testedService.existsChannel(channelToCheck);
//
//        //then after
//        Assertions.assertFalse(result);
//    }

}