package org.faust.chat;

import org.faust.chat.exception.ChannelUnknownException;
import org.faust.chat.external.ChannelService;
import org.faust.chat.external.ChatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRepositoryProxyServiceTest {

    @Mock
    ChannelService channelService;

    @Mock
    ChatService chatService;

    @InjectMocks
    ChatRepositoryProxyService testedService;

    @Test
    public void whenGetMessageFromExistingChannelThenReturnCorrect() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID beforeMessageId = UUID.randomUUID();
        UUID afterMessageId = UUID.randomUUID();
        int limit = 12;
        Collection<Message> expectedResult = Collections.EMPTY_LIST;

        when(channelService.existsChannel(channelId)).thenReturn(true);
        when(chatService.getMessages(channelId, beforeMessageId, afterMessageId, limit)).thenReturn(expectedResult);
        // when
        Collection<Message> actualResult = testedService.getAllMessages(channelId, beforeMessageId, afterMessageId, limit);

        // then
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void whenGetMessageFromNotExistingChannelThenException() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID beforeMessageId = UUID.randomUUID();
        UUID afterMessageId = UUID.randomUUID();
        int limit = 12;

        when(channelService.existsChannel(channelId)).thenReturn(false);

        // when-then
        assertThrows(ChannelUnknownException.class, () -> testedService.getAllMessages(channelId, beforeMessageId, afterMessageId, limit));
    }
}