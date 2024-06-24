package org.faust.chat.keycloak;

import org.faust.chat.user.UserDetails;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class KeycloakRepository {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public KeycloakRepository(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public Collection<UserDetails> getUsers() {
        return keycloak
                .realm(realm)
                .users()
                .list()
                .stream().map(representation -> new UserDetails(
                        UUID.fromString(representation.getId()),
                        representation.getUsername()
                ))
                .collect(Collectors.toList());
    }
}
