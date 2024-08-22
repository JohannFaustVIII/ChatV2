package org.faust.chat.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    public void whenGetStatusThenCallsRepositoryCorrectlyAndOnce() {
        // given

        // when
        userService.getUsers();
        // then
        verify(userRepository, times(1)).getUsers();
    }

    @Test
    public void whenSetOnlineThenCallsRepositoryCorrectlyAndOnce() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "RandomUser";
        // when
        userService.setActive(userId, username);
        // then
        verify(userRepository, times(1)).setActive(userId, username);
    }

    @Test
    public void whenSetAfkThenCallsRepositoryCorrectlyAndOnce() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "RandomUser";
        // when
        userService.setAfk(userId, username);
        // then
        verify(userRepository, times(1)).setAfk(userId, username);
    }

    @Test
    public void whenSetOfflineThenCallsRepositoryCorrectlyAndOnce() {
        // given
        UUID userId = UUID.randomUUID();
        String username = "RandomUser";
        // when
        userService.setOffline(userId, username);
        // then
        verify(userRepository, times(1)).setOffline(userId, username);
    }


    // TODO: think, because tests below are more like repository tests than the service tests

    @Test
    public void whenGetNoStatusInfoThenEmptyCollection() {

    }

    @Test
    public void whenGetExistingStatusInfoThenAllReturned() {

    }

    @Test
    public void whenNoUserDataThenReturnOnlyOffline() {

    }

    @Test
    public void whenUserSetOfflineThenReturnOnlyOffline() {

    }

    @Test
    public void whenUserSetAfkThenReturnOnlyAfk() {

    }

    @Test
    public void whenUserSetOnlineThenReturnOnlyOnline() {

    }

    @Test
    public void whenUserSetOfflineAndOnlineThenReturnOnlyOnline() {

    }

    @Test
    public void whenUserSetOfflineAndAfkThenReturnOnlyAfk() {

    }

    @Test
    public void whenUserSetAfkAndOnlineThenReturnOnlyOnline() {

    }

    @Test
    public void whenUserSetOfflineAndAfkAndOnlineThenReturnOnlyOnline() {

    }

}