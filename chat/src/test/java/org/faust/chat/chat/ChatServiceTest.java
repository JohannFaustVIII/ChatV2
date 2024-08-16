package org.faust.chat.chat;

import org.faust.chat.channel.ChannelService;
import org.faust.chat.exception.ChannelUnknownException;
import org.faust.chat.exception.InvalidPermissionsException;
import org.faust.chat.exception.MessageUnknownException;
import org.faust.chat.exception.UserUnknownException;
import org.faust.chat.keycloak.KeycloakService;
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
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChatServiceTest {

    @Mock(strictness = Mock.Strictness.WARN)
    MessageRepository messageRepository;

    @Mock
    ChannelService channelService;

    @Mock
    KeycloakService keycloakService;

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
        when(keycloakService.existsUser(senderId)).thenReturn(true);


        ChatService testedService = new ChatService(messageRepository, channelService, keycloakService);

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
        //given
        String messageToAdd = "Random message 1";
        UUID channel = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "random user";

        when(channelService.existsChannel(channel)).thenReturn(false);
        when(keycloakService.existsUser(senderId)).thenReturn(true);

        ChatService testedService = new ChatService(messageRepository, channelService, keycloakService);

        //when-then
        Assertions.assertThrows(ChannelUnknownException.class, () -> testedService.addMessage(channel, senderName, senderId, messageToAdd));
    }

    @Test
    public void whenAddMessageByNotExistingUserThenException() {
        //given
        String messageToAdd = "Random message 1";
        UUID channel = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "random user";

        when(channelService.existsChannel(channel)).thenReturn(true);
        when(keycloakService.existsUser(senderId)).thenReturn(false);

        ChatService testedService = new ChatService(messageRepository, channelService, keycloakService);

        //when-then
        Assertions.assertThrows(UserUnknownException.class, () -> testedService.addMessage(channel, senderName, senderId, messageToAdd));
    }

    @Test
    public void whenAddMessageByNotExistingUserAndNotExistingChannelThenException() {
        //given
        String messageToAdd = "Random message 1";
        UUID channel = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "random user";

        when(channelService.existsChannel(channel)).thenReturn(false);
        when(keycloakService.existsUser(senderId)).thenReturn(false);

        ChatService testedService = new ChatService(messageRepository, channelService, keycloakService);

        //when-then
        Assertions.assertThrows(UserUnknownException.class, () -> testedService.addMessage(channel, senderName, senderId, messageToAdd));
    }


    @Test
    public void whenEditMessageThenEdited() {
        // given
        String original = "Original message";
        String message = "Edited message 1";
        UUID channel = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID user = UUID.randomUUID();

        AtomicReference<Message> originalMessage = new AtomicReference<>(new Message(messageId, channel, "User", original, null, null, user));

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> originalMessage.get());
        doAnswer(inv -> {
            originalMessage.set(new Message(
                    messageId,
                    channel,
                    "User",
                    inv.getArgument(1),
                    null,
                    null,
                    user
            ));
            return null;
        }).when(messageRepository).editMessage(messageId, message);
        when(messageRepository.getAllMessages(channel, null, null, 10)).thenAnswer(inv -> {
            List<Message> messages = new ArrayList<>();
            messages.add(originalMessage.get());
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(keycloakService.existsUser(user)).thenReturn(true);


        ChatService testedService = new ChatService(messageRepository, channelService, keycloakService);

        // when
        testedService.editMessage(channel, messageId, user, message);

        // then
        Collection<Message> resultChannels = testedService.getMessages(channel);
        Assertions.assertEquals(resultChannels.size(), 1);

        Message resultMessage = resultChannels.iterator().next();
        Assertions.assertEquals(resultMessage.message(), message);
        Assertions.assertEquals(resultMessage.channelId(), channel);
        Assertions.assertEquals(resultMessage.id(), messageId);
        Assertions.assertEquals(resultMessage.senderId(), user);
    }

    @Test
    public void whenEditMessageNotExistingMessageThenException() {
        // given
        String message = "Edited message 1";
        UUID channel = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID user = UUID.randomUUID();

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> null);
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(keycloakService.existsUser(user)).thenReturn(true);

        ChatService testedService = new ChatService(messageRepository, channelService, keycloakService);

        // when-then
        Assertions.assertThrows(MessageUnknownException.class, () -> testedService.editMessage(channel, messageId, user, message));
    }

    @Test
    public void whenEditMessageToNotExistingChannelThenException() {
        // given
        String original = "Original message";
        String message = "Edited message 1";
        UUID channel = UUID.randomUUID();
        UUID channel2 = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID user = UUID.randomUUID();

        AtomicReference<Message> originalMessage = new AtomicReference<>(new Message(messageId, channel2, "User", original, null, null, user));

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> originalMessage.get());
        doAnswer(inv -> {
            originalMessage.set(new Message(
                    messageId,
                    channel2,
                    "User",
                    inv.getArgument(1),
                    null,
                    null,
                    user
            ));
            return null;
        }).when(messageRepository).editMessage(messageId, message);
        when(messageRepository.getAllMessages(channel2, null, null, 10)).thenAnswer(inv -> {
            List<Message> messages = new ArrayList<>();
            messages.add(originalMessage.get());
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(false);
        when(channelService.existsChannel(channel2)).thenReturn(true);
        when(keycloakService.existsUser(user)).thenReturn(true);

        ChatService testedService = new ChatService(messageRepository, channelService, keycloakService);

        // when
        Assertions.assertThrows(ChannelUnknownException.class, () -> testedService.editMessage(channel, messageId, user, message));

        // then
        Collection<Message> resultChannels = testedService.getMessages(channel2);
        Assertions.assertEquals(resultChannels.size(), 1);

        Message resultMessage = resultChannels.iterator().next();
        Assertions.assertEquals(resultMessage.message(), original);
        Assertions.assertEquals(resultMessage.channelId(), channel2);
        Assertions.assertEquals(resultMessage.id(), messageId);
        Assertions.assertEquals(resultMessage.senderId(), user);
    }

    @Test
    public void whenEditMessageToWrongChannelThenException() {
        // given
        String original = "Original message";
        String message = "Edited message 1";
        UUID channel = UUID.randomUUID();
        UUID channel2 = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID user = UUID.randomUUID();

        AtomicReference<Message> originalMessage = new AtomicReference<>(new Message(messageId, channel2, "User", original, null, null, user));

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> originalMessage.get());
        doAnswer(inv -> {
            originalMessage.set(new Message(
                    messageId,
                    channel2,
                    "User",
                    inv.getArgument(1),
                    null,
                    null,
                    user
            ));
            return null;
        }).when(messageRepository).editMessage(messageId, message);
        when(messageRepository.getAllMessages(channel2, null, null, 10)).thenAnswer(inv -> {
            List<Message> messages = new ArrayList<>();
            messages.add(originalMessage.get());
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(channelService.existsChannel(channel2)).thenReturn(true);
        when(keycloakService.existsUser(user)).thenReturn(true);

        ChatService testedService = new ChatService(messageRepository, channelService, keycloakService);

        // when
        Assertions.assertThrows(MessageUnknownException.class, () -> testedService.editMessage(channel, messageId, user, message));

        // then
        Collection<Message> resultChannels = testedService.getMessages(channel2);
        Assertions.assertEquals(resultChannels.size(), 1);

        Message resultMessage = resultChannels.iterator().next();
        Assertions.assertEquals(resultMessage.message(), original);
        Assertions.assertEquals(resultMessage.channelId(), channel2);
        Assertions.assertEquals(resultMessage.id(), messageId);
        Assertions.assertEquals(resultMessage.senderId(), user);
    }

    @Test
    public void whenEditMessageByNotExistingUserThenException() {
        // given
        String original = "Original message";
        String message = "Edited message 1";
        UUID channel = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID user = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        AtomicReference<Message> originalMessage = new AtomicReference<>(new Message(messageId, channel, "User", original, null, null, user));

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> originalMessage.get());
        doAnswer(inv -> {
            originalMessage.set(new Message(
                    messageId,
                    channel,
                    "User",
                    inv.getArgument(1),
                    null,
                    null,
                    user
            ));
            return null;
        }).when(messageRepository).editMessage(messageId, message);
        when(messageRepository.getAllMessages(channel, null, null, 10)).thenAnswer(inv -> {
            List<Message> messages = new ArrayList<>();
            messages.add(originalMessage.get());
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(keycloakService.existsUser(user)).thenReturn(true);
        when(keycloakService.existsUser(user2)).thenReturn(false);

        ChatService testedService = new ChatService(messageRepository, channelService, keycloakService);

        // when
        Assertions.assertThrows(UserUnknownException.class, () -> testedService.editMessage(channel, messageId, user2, message));

        // then
        Collection<Message> resultChannels = testedService.getMessages(channel);
        Assertions.assertEquals(resultChannels.size(), 1);

        Message resultMessage = resultChannels.iterator().next();
        Assertions.assertEquals(resultMessage.message(), original);
        Assertions.assertEquals(resultMessage.channelId(), channel);
        Assertions.assertEquals(resultMessage.id(), messageId);
        Assertions.assertEquals(resultMessage.senderId(), user);
    }

    @Test
    public void whenEditMessageByNotPermittedUserThenException() {
        // given
        String original = "Original message";
        String message = "Edited message 1";
        UUID channel = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID user = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        AtomicReference<Message> originalMessage = new AtomicReference<>(new Message(messageId, channel, "User", original, null, null, user));

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> originalMessage.get());
        doAnswer(inv -> {
            originalMessage.set(new Message(
                    messageId,
                    channel,
                    "User",
                    inv.getArgument(1),
                    null,
                    null,
                    user
            ));
            return null;
        }).when(messageRepository).editMessage(messageId, message);
        when(messageRepository.getAllMessages(channel, null, null, 10)).thenAnswer(inv -> {
            List<Message> messages = new ArrayList<>();
            messages.add(originalMessage.get());
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(keycloakService.existsUser(user)).thenReturn(true);
        when(keycloakService.existsUser(user2)).thenReturn(true);

        ChatService testedService = new ChatService(messageRepository, channelService, keycloakService);

        // when
        Assertions.assertThrows(InvalidPermissionsException.class, () -> testedService.editMessage(channel, messageId, user2, message));

        // then
        Collection<Message> resultChannels = testedService.getMessages(channel);
        Assertions.assertEquals(resultChannels.size(), 1);

        Message resultMessage = resultChannels.iterator().next();
        Assertions.assertEquals(resultMessage.message(), original);
        Assertions.assertEquals(resultMessage.channelId(), channel);
        Assertions.assertEquals(resultMessage.id(), messageId);
        Assertions.assertEquals(resultMessage.senderId(), user);
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