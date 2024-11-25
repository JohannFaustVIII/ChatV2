package org.faust.keycloak;

import org.springframework.stereotype.Service;

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
}
