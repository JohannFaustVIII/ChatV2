package org.faust.chat;

import org.faust.chat.command.AddMessage;
import org.faust.chat.command.DeleteMessage;
import org.faust.chat.command.EditMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    KafkaTemplate kafkaTemplate;

    @InjectMocks
    ChatService testedService;

    @Captor
    ArgumentCaptor<Object> argumentCaptor;

    @Test
    public void whenAddMessageThenSendCommandToKafka() {
        // given
        UUID channelId = UUID.randomUUID();
        String senderName = "Random sender";
        UUID senderId = UUID.randomUUID();
        String message = "Random message";
        UUID tokenId = UUID.randomUUID();

        // when
        testedService.addMessage(channelId, senderName, senderId, tokenId, message);

        // then
        verify(kafkaTemplate).send(eq("CHAT_REQUEST"), eq(channelId.toString()), argumentCaptor.capture());
        Object result = argumentCaptor.getValue();
        assertInstanceOf(AddMessage.class, result);
        AddMessage resultMessage = (AddMessage) result;
        assertEquals(channelId, resultMessage.channel());
        assertEquals(senderName, resultMessage.sender());
        assertEquals(senderId, resultMessage.senderId());
        assertEquals(message, resultMessage.message());
        assertEquals(tokenId, resultMessage.tokenId());
    }

    @Test
    public void whenEditMessageThenSendCommandToKafka() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String message = "New message";
        UUID tokenId = UUID.randomUUID();

        // when
        testedService.editMessage(channelId, messageId, senderId, tokenId, message);

        // then
        verify(kafkaTemplate).send(eq("CHAT_REQUEST"), eq(channelId.toString()), argumentCaptor.capture());
        Object result = argumentCaptor.getValue();
        assertInstanceOf(EditMessage.class, result);
        EditMessage resultMessage = (EditMessage) result;
        assertEquals(channelId, resultMessage.channel());
        assertEquals(messageId, resultMessage.messageId());
        assertEquals(senderId, resultMessage.userId());
        assertEquals(tokenId, resultMessage.tokenId());
        assertEquals(message, resultMessage.newMessage());
    }

    @Test
    public void whenDeleteMessageThenSendCommandToKafka() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID tokenId = UUID.randomUUID();

        // when
        testedService.deleteMessage(channelId, messageId, senderId, tokenId);

        // then
        verify(kafkaTemplate).send(eq("CHAT_REQUEST"), eq(channelId.toString()), argumentCaptor.capture());
        Object result = argumentCaptor.getValue();
        assertInstanceOf(DeleteMessage.class, result);
        DeleteMessage resultMessage = (DeleteMessage) result;
        assertEquals(channelId, resultMessage.channel());
        assertEquals(messageId, resultMessage.messageId());
        assertEquals(senderId, resultMessage.userId());
        assertEquals(tokenId, resultMessage.tokenId());
    }

}