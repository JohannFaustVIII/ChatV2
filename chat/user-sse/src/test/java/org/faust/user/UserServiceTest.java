package org.faust.user;

import org.faust.user.command.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    KafkaTemplate kafkaTemplate;

    @InjectMocks
    UserService testedService;

    @Captor
    ArgumentCaptor<Object> argumentCaptor;

    @Test
    public void whenSetOnlineThenSendCommandToKafka() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "Random username";
        // when
        testedService.setActive(userId, username);

        // then
        verify(kafkaTemplate).send(eq( "USER_ACTIVITY"), eq(userId.toString()), argumentCaptor.capture());
        Object result = argumentCaptor.getValue();
        assertInstanceOf(SetOnline.class, result);
        SetOnline commandResult = (SetOnline) result;
        assertEquals(userId, commandResult.userId());
        assertEquals(username, commandResult.username());
    }

    @Test
    public void whenSetAfkThenSendCommandToKafka() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "Random username";
        // when
        testedService.setAfk(userId, username);

        // then
        verify(kafkaTemplate).send(eq( "USER_ACTIVITY"), eq(userId.toString()), argumentCaptor.capture());
        Object result = argumentCaptor.getValue();
        assertInstanceOf(SetAfk.class, result);
        SetAfk commandResult = (SetAfk) result;
        assertEquals(userId, commandResult.userId());
        assertEquals(username, commandResult.username());
    }

    @Test
    public void whenSetOfflineThenSendCommandToKafka() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "Random username";
        // when
        testedService.setOffline(userId, username);

        // then
        verify(kafkaTemplate).send(eq( "USER_ACTIVITY"), eq(userId.toString()), argumentCaptor.capture());
        Object result = argumentCaptor.getValue();
        assertInstanceOf(SetOffline.class, result);
        SetOffline commandResult = (SetOffline) result;
        assertEquals(userId, commandResult.userId());
        assertEquals(username, commandResult.username());
    }

    @Test
    public void whenSetActivityHookThenSendCommandToKafka() {
        // given
        UUID userId = UUID.randomUUID();
        // when-then
        Flux fluxStream = testedService.setActivityHook(userId);

        // then
        verify(kafkaTemplate).send(eq( "USER_ACTIVITY"), eq(userId.toString()), argumentCaptor.capture());
        Object result = argumentCaptor.getValue();
        assertInstanceOf(IncreaseHook.class, result);
        IncreaseHook commandResult = (IncreaseHook) result;
        assertEquals(userId, commandResult.userId());

        // when-2
        StepVerifier.create(fluxStream).thenCancel().verify();

        // then-2
        verify(kafkaTemplate, times(2)).send(eq( "USER_ACTIVITY"), eq(userId.toString()), argumentCaptor.capture());
        result = argumentCaptor.getValue();
        assertInstanceOf(DecreaseHook.class, result);
        DecreaseHook commandResult2 = (DecreaseHook) result;
        assertEquals(userId, commandResult2.userId());
    }
}