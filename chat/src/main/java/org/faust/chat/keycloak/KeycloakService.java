package org.faust.chat.keycloak;

import org.faust.chat.user.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class KeycloakService {

    private final KeycloakRepository repository;

    public KeycloakService(KeycloakRepository repository) {
        this.repository = repository;
    }

    public Collection<UserDetails> getUsers() {
        return repository.getUsers();
    }
}
