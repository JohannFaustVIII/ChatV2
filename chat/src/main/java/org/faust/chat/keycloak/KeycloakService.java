package org.faust.chat.keycloak;

import org.faust.chat.exception.UserUnknownException;
import org.faust.chat.user.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class KeycloakService {

    private final KeycloakRepository repository;

    public KeycloakService(KeycloakRepository repository) {
        this.repository = repository;
    }

    public Collection<UserDetails> getUsers() {
        return repository.getUsers();
    }

    public UserDetails getUserInfo(UUID userId) {
        UserDetails result = repository.getUserInfo(userId);
        if (result == null) {
            throw new UserUnknownException();
        }
        return result;
    }

    public boolean existsUser(UUID userId) {
        return repository.existsUser(userId);
    }
}
