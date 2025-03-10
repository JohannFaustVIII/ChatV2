package org.faust.keycloak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.UUID;

// TODO: for E2E testing?

@RestController
@RequestMapping("/keycloak")
public class KeycloakController {

    private final KeycloakService keycloakService;

    public KeycloakController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @GetMapping("/exists/{id}")
    public boolean existsUser(@PathVariable("id") UUID userId) {
        return keycloakService.existsUser(userId);
    }

    @GetMapping("/details")
    public Collection<UserDetails> getUserDetails() {
        return keycloakService.getUsers();
    }

    @GetMapping("/details/{id}")
    public UserDetails getUser(@PathVariable UUID id) {
        return keycloakService.getUserInfo(id);
    }
}
