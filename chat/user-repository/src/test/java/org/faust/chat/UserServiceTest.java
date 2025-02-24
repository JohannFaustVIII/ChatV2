package org.faust.chat;

import org.faust.user.command.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserConsumer userService;

    @Test
    public void whenSetOnlineThenCallsRepositoryCorrectlyAndOnce() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "RandomUser";
        // when
        userService.setOnline(new SetOnline(userId, username));
        // then
        verify(userRepository, times(1)).setActive(userId, username);
    }

    @Test
    public void whenSetAfkThenCallsRepositoryCorrectlyAndOnce() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "RandomUser";
        // when
        userService.setAfk(new SetAfk(userId, username));
        // then
        verify(userRepository, times(1)).setAfk(userId, username);
    }

    @Test
    public void whenSetOfflineThenCallsRepositoryCorrectlyAndOnce() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "RandomUser";
        // when
        userService.setOffline(new SetOffline(userId, username));
        // then
        verify(userRepository, times(1)).setOffline(userId, username);
    }

    @Test
    public void whenIncreaseActivityThenCallsRepositoryCorrectlyAndOnce() {
        // given
        UUID userId = UUID.randomUUID();
        // when
        userService.increaseActivity(new IncreaseHook(userId));
        // then
        verify(userRepository, times(1)).incrementUserActivity(userId);
    }

    @Test
    public void whenDecreaseActivityThenCallsRepositoryCorrectlyAndOnce() {
        // given
        UUID userId = UUID.randomUUID();
        // when
        userService.decreaseActivity(new DecreaseHook(userId));
        // then
        verify(userRepository, times(1)).decrementUserActivity(userId);
    }
}