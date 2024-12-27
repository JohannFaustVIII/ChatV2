package org.faust.keycloak;

import org.faust.keycloak.exception.UserUnknownException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class KeycloakService {

    private final KeycloakRepository repository;

    public KeycloakService(KeycloakRepository repository) {
        this.repository = repository;
    }

    public boolean existsUser(UUID userId) {
        return repository.existsUser(userId);
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
}
