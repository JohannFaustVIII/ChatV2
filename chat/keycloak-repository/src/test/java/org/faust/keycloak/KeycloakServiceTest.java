package org.faust.keycloak;

import org.faust.keycloak.exception.UserUnknownException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakServiceTest {

    @Mock
    KeycloakRepository keycloakRepository;

    @Test
    public void whenGetNoUsersThenEmptyCollection() {
        // given
        List<UserDetails> details = new ArrayList<>();

        when(keycloakRepository.getUsers()).thenReturn(details);
        KeycloakService testedService = new KeycloakService(keycloakRepository);

        // when
        Collection<UserDetails> result = testedService.getUsers();

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.size(), 0);
    }

    @Test
    public void whenGetExistingUsersThenAllReturned() {
        // given
        List<UserDetails> details = new ArrayList<>();
        UserDetails user1 = new UserDetails(UUID.randomUUID(), "user1");
        UserDetails user2 = new UserDetails(UUID.randomUUID(), "user2");
        details.add(user1);
        details.add(user2);

        when(keycloakRepository.getUsers()).thenReturn(details);
        KeycloakService testedService = new KeycloakService(keycloakRepository);

        // when
        Collection<UserDetails> result = testedService.getUsers();

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.size(), 2);
        Iterator<UserDetails> it = result.iterator();
        Assertions.assertEquals(it.next(), user1);
        Assertions.assertEquals(it.next(), user2);
    }

    @Test
    public void whenGetExistingUserThenReturned() {
        // given
        UUID userId = UUID.randomUUID();
        String userName = "user1";

        when(keycloakRepository.getUserInfo(userId)).thenReturn(new UserDetails(userId, userName));
        KeycloakService testedService = new KeycloakService(keycloakRepository);

        // when
        UserDetails result = testedService.getUserInfo(userId);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.id(), userId);
        Assertions.assertEquals(result.name(), userName);
    }

    @Test
    public void whenGetNotExistingUserThenException() {
        // given
        UUID userId = UUID.randomUUID();

        when(keycloakRepository.getUserInfo(userId)).thenReturn(null);
        KeycloakService testedService = new KeycloakService(keycloakRepository);

        //when-then
        Assertions.assertThrows(UserUnknownException.class, () -> testedService.getUserInfo(userId));
    }

}