package org.faust.chat.chat;

import org.faust.chat.channel.Channel;
import org.faust.chat.channel.ChannelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    MessageRepository messageRepository;

    @Mock
    ChannelService channelService;

    @Test
    public void whenAddMessageThenAdded() {
        //given
        String messageToAdd = "Random message 1";
        UUID channel = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "random user";

        Collection<Message> addedMessages = new ArrayList<>();
//        doReturn(addedMessages).when(messageRepository).getAllMessages(channel, any(UUID.class), any(UUID.class), 10); //TODO: something fails here
        lenient().when(messageRepository.getAllMessages(eq(channel), any(UUID.class), any(UUID.class), anyInt())).thenReturn(addedMessages);
        lenient().doAnswer(invocation -> {
            addedMessages.add(invocation.getArgument(0));
            return null;
        }).when(messageRepository).addMessage(any(Message.class));
        lenient().when(channelService.existsChannel(channel)).thenReturn(true);


        ChatService testedService = new ChatService(messageRepository, channelService);

        //then before
        Assertions.assertEquals(testedService.getMessages(channel).size(), 0);
        Assertions.assertEquals(testedService.getMessages(channel), addedMessages);

        //when

        testedService.addMessage(channel, senderName, senderId, messageToAdd);

        //then after
        lenient().when(messageRepository.getAllMessages(eq(channel), any(UUID.class), any(UUID.class), anyInt())).thenReturn(addedMessages);
        Collection<Message> resultChannels = testedService.getMessages(channel); // AND HERE IS A COPY?!
        Assertions.assertEquals(resultChannels, addedMessages);
        Assertions.assertEquals(resultChannels.size(), 1);
        Assertions.assertTrue(resultChannels.stream().anyMatch(c -> c.message().equals(messageToAdd)));
        Assertions.assertTrue(resultChannels.stream().anyMatch(c -> c.channelId().equals(channel)));
        Assertions.assertTrue(resultChannels.stream().anyMatch(c -> c.sender().equals(senderName)));
        Assertions.assertTrue(resultChannels.stream().anyMatch(c -> c.senderId().equals(senderId)));
    }

    @Test
    public void whenAddMessageToNotExistingChannelThenException() {

    }

    @Test
    public void whenAddMessageByNotExistingUserThenException() {

    }

    @Test
    public void whenEditMessageThenAdded() {

    }

    @Test
    public void whenEditMessageToNotExistingChannelThenException() {

    }

    @Test
    public void whenEditMessageByNotExistingUserThenException() {

    }

    @Test
    public void whenEditMessageByNotPermittedUserThenException() {

    }

    @Test
    public void whenDeleteMessageThenAdded() {

    }

    @Test
    public void whenDeleteMessageToNotExistingChannelThenException() {

    }

    @Test
    public void whenDeleteMessageByNotExistingUserThenException() {

    }

    @Test
    public void whenDeleteMessageByNotPermittedUserThenException() {

    }

    @Test
    public void whenGetMessagesFromNotExistingChannelThenException() {

    }

    @Test
    public void whenGetNoMessagesThenEmptyCollection() {

    }

    @Test
    public void whenGetMessagesThenAllReturned() {

    }

    @Test
    public void whenGetLimitedMessagesThenReturnLastLimitedMessages() {

    }

    @Test
    public void whenGetLimitedNotEnoughMessagesThenReturnAll() {

    }

    @Test
    public void whenGetMessagesBeforeGivenAndLimitedThenReturnLimitedMessagesBeforeGiven() {

    }

    @Test
    public void whenGetMessagesBeforeGivenAndLimitedButNotEnoughThenReturnAllMessagesBeforeGiven() {

    }

    @Test
    public void whenGetMessagesAfterGivenAndLimitedThenReturnLimitedMessagesAfterGiven() {

    }

    @Test
    public void whenGetMessagesAfterGivenAndLimitedButNotEnoughThenReturnAllMessagesAfterGiven() {

    }

    @Test
    public void whenGetMessagesBetweenGivenAndLimitedThenReturnLimitedMessagesBeforeGiven() {

    }

    @Test
    public void whenGetMessagesBetweenGivenAndLimitedButNotEnoughThenReturnAllMessagesBetweenGiven() {

    }

    @Test
    public void whenGetMessagesBetweenIsEmptyThenEmptyCollection() {

    }

    @Test
    public void whenGetMessagesBetweenInIncorrectOrderButThenException() {

    }
    // TODO: what if by not existing user and not existing channel? what takes priority?
}