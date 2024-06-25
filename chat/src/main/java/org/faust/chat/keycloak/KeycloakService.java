package org.faust.chat.keycloak;

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
        return repository.getUserInfo(userId);
    }
}
