package org.faust.chat.chat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatServiceTest {

    @Test
    public void whenAddMessageThenAdded() {

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