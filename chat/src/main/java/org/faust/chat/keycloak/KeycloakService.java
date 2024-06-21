package org.faust.chat.keycloak;

import org.springframework.stereotype.Service;

@Service
public class KeycloakService {

    private final KeycloakRepository repository;

    public KeycloakService(KeycloakRepository repository) {
        this.repository = repository;
    }

    public void getUsers() {
        repository.getUsers();
    }
}
