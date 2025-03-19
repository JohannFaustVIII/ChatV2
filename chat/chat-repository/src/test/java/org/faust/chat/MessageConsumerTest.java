package org.faust.chat;

import org.faust.chat.command.AddMessage;
import org.faust.chat.command.DeleteMessage;
import org.faust.chat.command.EditMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageConsumerTest {

    @Mock
    MessageRepository messageRepository;

    @Mock
    KafkaTemplate<String, org.faust.sse.Message> kafkaTemplate;

    @InjectMocks
    MessageConsumer testedConsumer;

    @Captor
    ArgumentCaptor<Message> messageCaptor;

    @Captor
    ArgumentCaptor<org.faust.sse.Message> sseCaptor;

    @Test
    public void whenAddMessageThenAdded() {
        //given
        String messageToAdd = "Random message 1";
        UUID channel = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "random user";
        UUID tokenId = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();

        //when
        testedConsumer.addMessage(new AddMessage(tokenId, channel, senderName, senderId, messageToAdd, time));

        //then
        verify(messageRepository).addMessage(messageCaptor.capture());
        Message addedMessage = messageCaptor.getValue();
        Assertions.assertEquals(messageToAdd, addedMessage.message());
        Assertions.assertEquals(channel, addedMessage.channelId());
        Assertions.assertEquals(senderId, addedMessage.senderId());
        Assertions.assertEquals(senderName, addedMessage.sender());
        Assertions.assertEquals(time, addedMessage.serverTime());
    }

    @Test
    public void whenEditMessageThenEdited() {
        // given
        String original = "Original message";
        String message = "Edited message 1";
        UUID messageId = UUID.randomUUID();
        UUID channel = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "random user";
        UUID tokenId = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> new Message(messageId, channel, senderName, original, time, null, senderId));
        // when
        testedConsumer.editMessage(new EditMessage(tokenId, channel, messageId, senderId, message, time));

        // then
        verify(messageRepository).editMessage(eq(messageId), eq(message), eq(time));
    }

    @Test
    public void whenEditMessageNotExistingMessageThenException() {
        // given
        String original = "Original message";
        String message = "Edited message 1";
        UUID messageId = UUID.randomUUID();
        UUID channel = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "random user";
        UUID tokenId = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> null);

        // when
        testedConsumer.editMessage(new EditMessage(tokenId, channel, messageId, senderId, message, time));

        // then
        verify(messageRepository, never()).editMessage(any(), any(), any());
        verify(kafkaTemplate).send(eq("SSE_EVENTS"), eq(tokenId.toString()), sseCaptor.capture());
        org.faust.sse.Message result = sseCaptor.getValue();
        Assertions.assertEquals(tokenId, result.tokenId());
        Assertions.assertEquals("Requested message to edit is unknown.", result.message());
    }

    @Test
    public void whenEditMessageToWrongChannelThenException() {
        // given
        String original = "Original message";
        String message = "Edited message 1";
        UUID messageId = UUID.randomUUID();
        UUID channel = UUID.randomUUID();
        UUID channel2 = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "random user";
        UUID tokenId = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> new Message(messageId, channel2, senderName, original, LocalDateTime.now(), null, senderId));

        // when
        testedConsumer.editMessage(new EditMessage(tokenId, channel, messageId, senderId, message, time));

        // then
        verify(messageRepository, never()).editMessage(any(), any(), any());
        verify(kafkaTemplate).send(eq("SSE_EVENTS"), eq(tokenId.toString()), sseCaptor.capture());
        org.faust.sse.Message result = sseCaptor.getValue();
        Assertions.assertEquals(tokenId, result.tokenId());
        Assertions.assertEquals("Requested message to edit is unknown.", result.message());
    }

    @Test
    public void whenEditMessageByNotPermittedUserThenException() {
        // given
        String original = "Original message";
        String message = "Edited message 1";
        UUID messageId = UUID.randomUUID();
        UUID channel = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID senderId2 = UUID.randomUUID();
        String senderName = "random user";
        UUID tokenId = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> new Message(messageId, channel, senderName, original, LocalDateTime.now(), null, senderId2));

        // when
        testedConsumer.editMessage(new EditMessage(tokenId, channel, messageId, senderId, message, time));

        // then
        verify(messageRepository, never()).editMessage(any(), any(), any());
        verify(kafkaTemplate).send(eq("SSE_EVENTS"), eq(tokenId.toString()), sseCaptor.capture());
        org.faust.sse.Message result = sseCaptor.getValue();
        Assertions.assertEquals(tokenId, result.tokenId());
        Assertions.assertEquals("Invalid permissions to edit the message.", result.message());
    }

    @Test
    public void whenDeleteMessageThenRemoved() {
        // given
        String original = "Original message";
        UUID messageId = UUID.randomUUID();
        UUID channel = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "random user";
        UUID tokenId = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> new Message(messageId, channel, senderName, original, time, null, senderId));

        // when
        testedConsumer.deleteMessage(new DeleteMessage(tokenId, channel, messageId, senderId));

        // then
        verify(messageRepository).deleteMessage(eq(messageId));
    }

    @Test
    public void whenDeleteNotExistingMessageThenException() {
        // given
        String original = "Original message";
        UUID messageId = UUID.randomUUID();
        UUID channel = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "random user";
        UUID tokenId = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> null);

        // when
        testedConsumer.deleteMessage(new DeleteMessage(tokenId, channel, messageId, senderId));

        // then
        verify(messageRepository, never()).deleteMessage(any());
        verify(kafkaTemplate).send(eq("SSE_EVENTS"), eq(tokenId.toString()), sseCaptor.capture());
        org.faust.sse.Message result = sseCaptor.getValue();
        Assertions.assertEquals(tokenId, result.tokenId());
        Assertions.assertEquals("Requested message to delete is unknown.", result.message());
    }

    @Test
    public void whenDeleteMessageFromWrongChannelThenException() {
        // given
        String original = "Original message";
        UUID messageId = UUID.randomUUID();
        UUID channel = UUID.randomUUID();
        UUID channel2 = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "random user";
        UUID tokenId = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> new Message(messageId, channel2, senderName, original, time, null, senderId));

        // when
        testedConsumer.deleteMessage(new DeleteMessage(tokenId, channel, messageId, senderId));

        // then
        verify(messageRepository, never()).deleteMessage(any());
        verify(kafkaTemplate).send(eq("SSE_EVENTS"), eq(tokenId.toString()), sseCaptor.capture());
        org.faust.sse.Message result = sseCaptor.getValue();
        Assertions.assertEquals(tokenId, result.tokenId());
        Assertions.assertEquals("Requested message to delete is unknown.", result.message());
    }

    @Test
    public void whenDeleteMessageByNotPermittedUserThenException() {
        // given
        String original = "Original message";
        UUID messageId = UUID.randomUUID();
        UUID channel = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID senderId2 = UUID.randomUUID();
        String senderName = "random user";
        UUID tokenId = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();

        when(messageRepository.getMessage(messageId)).thenAnswer(a -> new Message(messageId, channel, senderName, original, time, null, senderId2));

        // when
        testedConsumer.deleteMessage(new DeleteMessage(tokenId, channel, messageId, senderId));

        // then
        verify(messageRepository, never()).deleteMessage(any());
        verify(kafkaTemplate).send(eq("SSE_EVENTS"), eq(tokenId.toString()), sseCaptor.capture());
        org.faust.sse.Message result = sseCaptor.getValue();
        Assertions.assertEquals(tokenId, result.tokenId());
        Assertions.assertEquals("Invalid permissions to remove the message.", result.message());
    }
}