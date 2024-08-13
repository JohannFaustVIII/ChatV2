package org.faust.chat.chat;

import org.faust.chat.channel.ChannelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChatServiceTest {

    @Mock(strictness = Mock.Strictness.WARN)
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

        List<Message> addedMessages = new ArrayList<>();
        when(messageRepository.getAllMessages(any(UUID.class), eq(null), eq(null), eq(10))).thenReturn(addedMessages);
        doAnswer(invocation -> {
            System.out.println("I was called");
            addedMessages.add(invocation.getArgument(0));
            return null;
        }).when(messageRepository).addMessage(any(Message.class));
        when(channelService.existsChannel(channel)).thenReturn(true);


        ChatService testedService = new ChatService(messageRepository, channelService);

        //then before
        Assertions.assertEquals(testedService.getMessages(channel).size(), 0);

        //when
        testedService.addMessage(channel, senderName, senderId, messageToAdd);

        //then after
        Collection<Message> resultChannels = testedService.getMessages(channel);
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