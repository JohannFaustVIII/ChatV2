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
import org.mockito.InjectMocks;
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
class ChatServiceTest {

    @Mock
    MessageRepository messageRepository;

    @Mock
    ChannelService channelService;

    @Mock
    KeycloakService keycloakService;

    @InjectMocks
    ChatService testedService;

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

        when(keycloakService.existsUser(senderId)).thenReturn(false);

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

        when(keycloakService.existsUser(senderId)).thenReturn(false);

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

        // when
        testedService.editMessage(channel, messageId, user, message);

        // then
        Collection<Message> resultMessages = testedService.getMessages(channel);
        Assertions.assertEquals(resultMessages.size(), 1);

        Message resultMessage = resultMessages.iterator().next();
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

        when(messageRepository.getAllMessages(channel2, null, null, 10)).thenAnswer(inv -> {
            List<Message> messages = new ArrayList<>();
            messages.add(originalMessage.get());
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(false);
        when(channelService.existsChannel(channel2)).thenReturn(true);

        // when
        Assertions.assertThrows(ChannelUnknownException.class, () -> testedService.editMessage(channel, messageId, user, message));

        // then
        Collection<Message> resultMessages = testedService.getMessages(channel2);
        Assertions.assertEquals(resultMessages.size(), 1);

        Message resultMessage = resultMessages.iterator().next();
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
        when(messageRepository.getAllMessages(channel2, null, null, 10)).thenAnswer(inv -> {
            List<Message> messages = new ArrayList<>();
            messages.add(originalMessage.get());
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(channelService.existsChannel(channel2)).thenReturn(true);
        when(keycloakService.existsUser(user)).thenReturn(true);

        // when
        Assertions.assertThrows(MessageUnknownException.class, () -> testedService.editMessage(channel, messageId, user, message));

        // then
        Collection<Message> resultMessages = testedService.getMessages(channel2);
        Assertions.assertEquals(resultMessages.size(), 1);

        Message resultMessage = resultMessages.iterator().next();
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

        when(messageRepository.getAllMessages(channel, null, null, 10)).thenAnswer(inv -> {
            List<Message> messages = new ArrayList<>();
            messages.add(originalMessage.get());
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(keycloakService.existsUser(user2)).thenReturn(false);

        // when
        Assertions.assertThrows(UserUnknownException.class, () -> testedService.editMessage(channel, messageId, user2, message));

        // then
        Collection<Message> resultMessages = testedService.getMessages(channel);
        Assertions.assertEquals(resultMessages.size(), 1);

        Message resultMessage = resultMessages.iterator().next();
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
        when(messageRepository.getAllMessages(channel, null, null, 10)).thenAnswer(inv -> {
            List<Message> messages = new ArrayList<>();
            messages.add(originalMessage.get());
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(keycloakService.existsUser(user2)).thenReturn(true);

        // when
        Assertions.assertThrows(InvalidPermissionsException.class, () -> testedService.editMessage(channel, messageId, user2, message));

        // then
        Collection<Message> resultMessages = testedService.getMessages(channel);
        Assertions.assertEquals(resultMessages.size(), 1);

        Message resultMessage = resultMessages.iterator().next();
        Assertions.assertEquals(resultMessage.message(), original);
        Assertions.assertEquals(resultMessage.channelId(), channel);
        Assertions.assertEquals(resultMessage.id(), messageId);
        Assertions.assertEquals(resultMessage.senderId(), user);
    }

    @Test
    public void whenDeleteMessageThenRemoved() {
        // given
        UUID channel = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID user = UUID.randomUUID();

        Message message = new Message(messageId, channel, "User", "Random text", null, null, user);
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        when(messageRepository.getMessage(messageId)).thenReturn(message);
        doAnswer(inv -> {
            messages.stream().filter(m -> m.id().equals(inv.getArgument(0))).findFirst().ifPresent(
                    messages::remove
            );
            return null;
        }).when(messageRepository).deleteMessage(messageId);
        when(messageRepository.getAllMessages(channel, null, null, 10)).thenAnswer(inv -> {
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(keycloakService.existsUser(user)).thenReturn(true);
        // when
        testedService.deleteMessage(channel, messageId, user);

        // then
        Collection<Message> result = testedService.getMessages(channel);
        Assertions.assertEquals(result.size(), 0);
    }

    @Test
    public void whenDeleteNotExistingMessageThenException() {
        // given
        UUID channel = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID user = UUID.randomUUID();

        UUID messageId2 = UUID.randomUUID();

        Message message = new Message(messageId, channel, "User", "Random text", null, null, user);
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        when(messageRepository.getAllMessages(channel, null, null, 10)).thenAnswer(inv -> {
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(keycloakService.existsUser(user)).thenReturn(true);
        // when
        Assertions.assertThrows(MessageUnknownException.class, () -> testedService.deleteMessage(channel, messageId2, user));

        // then
        Collection<Message> result = testedService.getMessages(channel);
        Assertions.assertEquals(result.size(), 1);

        Message resultMessage = result.iterator().next();
        Assertions.assertEquals(resultMessage.message(), "Random text");
        Assertions.assertEquals(resultMessage.channelId(), channel);
        Assertions.assertEquals(resultMessage.id(), messageId);
        Assertions.assertEquals(resultMessage.senderId(), user);
    }

    @Test
    public void whenDeleteMessageToNotExistingChannelThenException() {
        // given
        UUID channel = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID user = UUID.randomUUID();

        UUID channelId2 = UUID.randomUUID();

        Message message = new Message(messageId, channel, "User", "Random text", null, null, user);
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        when(messageRepository.getAllMessages(channel, null, null, 10)).thenAnswer(inv -> {
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(channelService.existsChannel(channelId2)).thenReturn(false);

        // when
        Assertions.assertThrows(ChannelUnknownException.class, () -> testedService.deleteMessage(channelId2, messageId, user));

        // then
        Collection<Message> result = testedService.getMessages(channel);
        Assertions.assertEquals(result.size(), 1);

        Message resultMessage = result.iterator().next();
        Assertions.assertEquals(resultMessage.message(), "Random text");
        Assertions.assertEquals(resultMessage.channelId(), channel);
        Assertions.assertEquals(resultMessage.id(), messageId);
        Assertions.assertEquals(resultMessage.senderId(), user);
    }

    @Test
    public void whenDeleteMessageFromWrongChannelThenException() {
        // given
        UUID channel = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID user = UUID.randomUUID();

        UUID channelId2 = UUID.randomUUID();

        Message message = new Message(messageId, channel, "User", "Random text", null, null, user);
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        when(messageRepository.getMessage(messageId)).thenReturn(message);
        when(messageRepository.getAllMessages(channel, null, null, 10)).thenAnswer(inv -> {
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(channelService.existsChannel(channelId2)).thenReturn(true);
        when(keycloakService.existsUser(user)).thenReturn(true);

        // when
        Assertions.assertThrows(MessageUnknownException.class, () -> testedService.deleteMessage(channelId2, messageId, user));

        // then
        Collection<Message> result = testedService.getMessages(channel);
        Assertions.assertEquals(result.size(), 1);

        Message resultMessage = result.iterator().next();
        Assertions.assertEquals(resultMessage.message(), "Random text");
        Assertions.assertEquals(resultMessage.channelId(), channel);
        Assertions.assertEquals(resultMessage.id(), messageId);
        Assertions.assertEquals(resultMessage.senderId(), user);
    }

    @Test
    public void whenDeleteMessageByNotExistingUserThenException() {
        // given
        UUID channel = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID user = UUID.randomUUID();

        UUID userId2 = UUID.randomUUID();

        Message message = new Message(messageId, channel, "User", "Random text", null, null, user);
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        when(messageRepository.getAllMessages(channel, null, null, 10)).thenAnswer(inv -> {
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(true);

        when(keycloakService.existsUser(userId2)).thenReturn(false);

        // when
        Assertions.assertThrows(UserUnknownException.class, () -> testedService.deleteMessage(channel, messageId, userId2));

        // then
        Collection<Message> result = testedService.getMessages(channel);
        Assertions.assertEquals(result.size(), 1);

        Message resultMessage = result.iterator().next();
        Assertions.assertEquals(resultMessage.message(), "Random text");
        Assertions.assertEquals(resultMessage.channelId(), channel);
        Assertions.assertEquals(resultMessage.id(), messageId);
        Assertions.assertEquals(resultMessage.senderId(), user);
    }

    @Test
    public void whenDeleteMessageByNotPermittedUserThenException() {
        // given
        UUID channel = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID user = UUID.randomUUID();

        UUID userId2 = UUID.randomUUID();

        Message message = new Message(messageId, channel, "User", "Random text", null, null, user);
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        when(messageRepository.getMessage(messageId)).thenReturn(message);
        when(messageRepository.getAllMessages(channel, null, null, 10)).thenAnswer(inv -> {
            return messages;
        });
        when(channelService.existsChannel(channel)).thenReturn(true);

        when(keycloakService.existsUser(userId2)).thenReturn(true);

        // when
        Assertions.assertThrows(InvalidPermissionsException.class, () -> testedService.deleteMessage(channel, messageId, userId2));

        // then
        Collection<Message> result = testedService.getMessages(channel);
        Assertions.assertEquals(result.size(), 1);

        Message resultMessage = result.iterator().next();
        Assertions.assertEquals(resultMessage.message(), "Random text");
        Assertions.assertEquals(resultMessage.channelId(), channel);
        Assertions.assertEquals(resultMessage.id(), messageId);
        Assertions.assertEquals(resultMessage.senderId(), user);
    }

    @Test
    public void whenGetMessagesFromNotExistingChannelThenException() {
        // given
        UUID channel = UUID.randomUUID();

        when(channelService.existsChannel(channel)).thenReturn(false);

        // when-then
        Assertions.assertThrows(ChannelUnknownException.class, () -> testedService.getMessages(channel));
    }

    @Test
    public void whenGetNoMessagesThenEmptyCollection() {
        // given
        UUID channel = UUID.randomUUID();

        List<Message> messages = new ArrayList<>();
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(messageRepository.getAllMessages(channel, null, null, 10)).thenReturn(messages);

        // when
        Collection<Message> result = testedService.getMessages(channel);

        // then
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void whenGetMessagesThenAllReturned() {
        // given
        UUID channel = UUID.randomUUID();

        List<Message> messages = new ArrayList<>();
        messages.add(new Message(UUID.randomUUID(), channel, "RandomSender", "RandomMessage", null, null, UUID.randomUUID()));
        when(channelService.existsChannel(channel)).thenReturn(true);
        when(messageRepository.getAllMessages(channel, null, null, 10)).thenReturn(messages);

        // when
        Collection<Message> result = testedService.getMessages(channel);

        // then
        Assertions.assertFalse(result.isEmpty());

        Message message = result.iterator().next();
        Assertions.assertEquals(message.channelId(), channel);
        Assertions.assertEquals(message.sender(), "RandomSender");
        Assertions.assertEquals(message.message(), "RandomMessage");
    }

    @Test
    public void whenGetLimitedMessagesThenCallsRepositoryCorrectlyAndOnce() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID messageBefore = UUID.randomUUID();
        UUID messageAfter = UUID.randomUUID();
        int limit = 123;

        when(channelService.existsChannel(channelId)).thenReturn(true);

        // when
        testedService.getMessages(channelId, messageBefore, messageAfter, limit);

        // then
        verify(messageRepository, times(1)).getAllMessages(channelId, messageBefore, messageAfter, limit);
    }
}