package org.faust.chat;

import org.faust.chat.external.KeycloakRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    KeycloakRepository keycloakRepository;

    @InjectMocks
    UserRepository testedRepository;

    @Test
    public void whenGetNoStatusInfoThenEmptyCollection() {
        // given
        when(keycloakRepository.getUsers()).thenReturn(Collections.emptyList());

        // when
        Map<UserStatus, Collection<UserDetails>> result = testedRepository.getUsers();

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(result.size(), 3);
        Assertions.assertTrue(result.get(UserStatus.OFFLINE).isEmpty());
        Assertions.assertTrue(result.get(UserStatus.AFK).isEmpty());
        Assertions.assertTrue(result.get(UserStatus.ONLINE).isEmpty());
    }

    @Test
    public void whenGetExistingStatusInfoThenAllReturned() {
        // given
        List<UserDetails> users = new ArrayList();
        users.add(new UserDetails(UUID.randomUUID(), "User 1"));
        users.add(new UserDetails(UUID.randomUUID(), "User 2"));
        when(keycloakRepository.getUsers()).thenReturn(users);

        // when
        Map<UserStatus, Collection<UserDetails>> result = testedRepository.getUsers();

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(result.size(), 3);
        Assertions.assertEquals(result.get(UserStatus.OFFLINE).size(), 2);
        Assertions.assertTrue(result.get(UserStatus.OFFLINE).stream().anyMatch(u -> u.name().equals("User 1")));
        Assertions.assertTrue(result.get(UserStatus.OFFLINE).stream().anyMatch(u -> u.name().equals("User 2")));
        Assertions.assertTrue(result.get(UserStatus.AFK).isEmpty());
        Assertions.assertTrue(result.get(UserStatus.ONLINE).isEmpty());
    }

    @Test
    public void whenNoUserDataThenReturnOnlyOffline() {
        // given
        List<UserDetails> users = new ArrayList();
        users.add(new UserDetails(UUID.randomUUID(), "User 1"));
        users.add(new UserDetails(UUID.randomUUID(), "User 2"));
        when(keycloakRepository.getUsers()).thenReturn(users);

        // when
        Map<UserStatus, Collection<UserDetails>> result = testedRepository.getUsers();

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(result.size(), 3);
        Assertions.assertEquals(result.get(UserStatus.OFFLINE).size(), 2);
        Assertions.assertTrue(result.get(UserStatus.OFFLINE).stream().anyMatch(u -> u.name().equals("User 1")));
        Assertions.assertTrue(result.get(UserStatus.OFFLINE).stream().anyMatch(u -> u.name().equals("User 2")));
        Assertions.assertTrue(result.get(UserStatus.AFK).isEmpty());
        Assertions.assertTrue(result.get(UserStatus.ONLINE).isEmpty());
    }

    @Test
    public void whenUserSetOfflineThenReturnOnlyOffline() {
        // given
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        List<UserDetails> users = new ArrayList();
        users.add(new UserDetails(user1, "User 1"));
        users.add(new UserDetails(user2, "User 2"));
        when(keycloakRepository.getUsers()).thenReturn(users);

        // when
        testedRepository.incrementUserActivity(user1);
        testedRepository.setOffline(user1, "User 1");
        Map<UserStatus, Collection<UserDetails>> result = testedRepository.getUsers();
        testedRepository.decrementUserActivity(user1);

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(result.size(), 3);
        Assertions.assertEquals(result.get(UserStatus.OFFLINE).size(), 2);
        Assertions.assertTrue(result.get(UserStatus.OFFLINE).stream().anyMatch(u -> u.name().equals("User 1")));
        Assertions.assertTrue(result.get(UserStatus.OFFLINE).stream().anyMatch(u -> u.name().equals("User 2")));
        Assertions.assertTrue(result.get(UserStatus.AFK).isEmpty());
        Assertions.assertTrue(result.get(UserStatus.ONLINE).isEmpty());
    }

    @Test
    public void whenUserSetAfkThenReturnOnlyAfk() {
        // given
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        List<UserDetails> users = new ArrayList();
        users.add(new UserDetails(user1, "User 1"));
        users.add(new UserDetails(user2, "User 2"));
        when(keycloakRepository.getUsers()).thenReturn(users);

        // when
        testedRepository.incrementUserActivity(user1);
        testedRepository.setAfk(user1, "User 1");
        Map<UserStatus, Collection<UserDetails>> result = testedRepository.getUsers();
        testedRepository.decrementUserActivity(user1);

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(result.size(), 3);
        Assertions.assertEquals(result.get(UserStatus.OFFLINE).size(), 1);
        Assertions.assertTrue(result.get(UserStatus.OFFLINE).stream().anyMatch(u -> u.name().equals("User 2")));
        Assertions.assertEquals(result.get(UserStatus.AFK).size(), 1);
        Assertions.assertTrue(result.get(UserStatus.AFK).stream().anyMatch(u -> u.name().equals("User 1")));
        Assertions.assertTrue(result.get(UserStatus.ONLINE).isEmpty());
    }

    @Test
    public void whenUserSetOnlineThenReturnOnlyOnline() {
        // given
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        List<UserDetails> users = new ArrayList();
        users.add(new UserDetails(user1, "User 1"));
        users.add(new UserDetails(user2, "User 2"));
        when(keycloakRepository.getUsers()).thenReturn(users);

        // when
        testedRepository.incrementUserActivity(user1);
        testedRepository.setActive(user1, "User 1");
        Map<UserStatus, Collection<UserDetails>> result = testedRepository.getUsers();
        testedRepository.decrementUserActivity(user1);

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(result.size(), 3);
        Assertions.assertEquals(result.get(UserStatus.OFFLINE).size(), 1);
        Assertions.assertTrue(result.get(UserStatus.OFFLINE).stream().anyMatch(u -> u.name().equals("User 2")));
        Assertions.assertTrue(result.get(UserStatus.AFK).isEmpty());
        Assertions.assertEquals(result.get(UserStatus.ONLINE).size(), 1);
        Assertions.assertTrue(result.get(UserStatus.ONLINE).stream().anyMatch(u -> u.name().equals("User 1")));
    }

    @Test
    public void whenUserSetOfflineAndOnlineThenReturnOnlyOnline() {
        // given
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        List<UserDetails> users = new ArrayList();
        users.add(new UserDetails(user1, "User 1"));
        users.add(new UserDetails(user2, "User 2"));
        when(keycloakRepository.getUsers()).thenReturn(users);

        // when
        testedRepository.incrementUserActivity(user1);
        testedRepository.setActive(user1, "User 1");
        testedRepository.setOffline(user1, "User 1");
        Map<UserStatus, Collection<UserDetails>> result = testedRepository.getUsers();
        testedRepository.decrementUserActivity(user1);

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(result.size(), 3);
        Assertions.assertEquals(result.get(UserStatus.OFFLINE).size(), 1);
        Assertions.assertTrue(result.get(UserStatus.OFFLINE).stream().anyMatch(u -> u.name().equals("User 2")));
        Assertions.assertTrue(result.get(UserStatus.AFK).isEmpty());
        Assertions.assertEquals(result.get(UserStatus.ONLINE).size(), 1);
        Assertions.assertTrue(result.get(UserStatus.ONLINE).stream().anyMatch(u -> u.name().equals("User 1")));
    }

    @Test
    public void whenUserSetOfflineAndAfkThenReturnOnlyAfk() {
        // given
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        List<UserDetails> users = new ArrayList();
        users.add(new UserDetails(user1, "User 1"));
        users.add(new UserDetails(user2, "User 2"));
        when(keycloakRepository.getUsers()).thenReturn(users);

        // when
        testedRepository.incrementUserActivity(user1);
        testedRepository.setAfk(user1, "User 1");
        testedRepository.setOffline(user1, "User 1");
        Map<UserStatus, Collection<UserDetails>> result = testedRepository.getUsers();
        testedRepository.decrementUserActivity(user1);

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(result.size(), 3);
        Assertions.assertEquals(result.get(UserStatus.OFFLINE).size(), 1);
        Assertions.assertTrue(result.get(UserStatus.OFFLINE).stream().anyMatch(u -> u.name().equals("User 2")));
        Assertions.assertEquals(result.get(UserStatus.AFK).size(), 1);
        Assertions.assertTrue(result.get(UserStatus.AFK).stream().anyMatch(u -> u.name().equals("User 1")));
        Assertions.assertTrue(result.get(UserStatus.ONLINE).isEmpty());
    }

    @Test
    public void whenUserSetAfkAndOnlineThenReturnOnlyOnline() {
        // given
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        List<UserDetails> users = new ArrayList();
        users.add(new UserDetails(user1, "User 1"));
        users.add(new UserDetails(user2, "User 2"));
        when(keycloakRepository.getUsers()).thenReturn(users);

        // when
        testedRepository.incrementUserActivity(user1);
        testedRepository.setActive(user1, "User 1");
        testedRepository.setAfk(user1, "User 1");
        Map<UserStatus, Collection<UserDetails>> result = testedRepository.getUsers();
        testedRepository.decrementUserActivity(user1);

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(result.size(), 3);
        Assertions.assertEquals(result.get(UserStatus.OFFLINE).size(), 1);
        Assertions.assertTrue(result.get(UserStatus.OFFLINE).stream().anyMatch(u -> u.name().equals("User 2")));
        Assertions.assertTrue(result.get(UserStatus.AFK).isEmpty());
        Assertions.assertEquals(result.get(UserStatus.ONLINE).size(), 1);
        Assertions.assertTrue(result.get(UserStatus.ONLINE).stream().anyMatch(u -> u.name().equals("User 1")));
    }

    @Test
    public void whenUserSetOfflineAndAfkAndOnlineThenReturnOnlyOnline() {
        // given
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        List<UserDetails> users = new ArrayList();
        users.add(new UserDetails(user1, "User 1"));
        users.add(new UserDetails(user2, "User 2"));
        when(keycloakRepository.getUsers()).thenReturn(users);

        // when
        testedRepository.incrementUserActivity(user1);
        testedRepository.setActive(user1, "User 1");
        testedRepository.setAfk(user1, "User 1");
        testedRepository.setOffline(user1, "User 1");
        Map<UserStatus, Collection<UserDetails>> result = testedRepository.getUsers();
        testedRepository.decrementUserActivity(user1);

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(result.size(), 3);
        Assertions.assertEquals(result.get(UserStatus.OFFLINE).size(), 1);
        Assertions.assertTrue(result.get(UserStatus.OFFLINE).stream().anyMatch(u -> u.name().equals("User 2")));
        Assertions.assertTrue(result.get(UserStatus.AFK).isEmpty());
        Assertions.assertEquals(result.get(UserStatus.ONLINE).size(), 1);
        Assertions.assertTrue(result.get(UserStatus.ONLINE).stream().anyMatch(u -> u.name().equals("User 1")));
    }
}