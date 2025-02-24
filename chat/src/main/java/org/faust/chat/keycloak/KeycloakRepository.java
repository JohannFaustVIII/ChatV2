package org.faust.chat.keycloak;

import jakarta.ws.rs.NotFoundException;
import org.faust.chat.user.UserDetails;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class KeycloakRepository {

    private final Keycloak keycloak;

    private final String realm;

    public KeycloakRepository(Keycloak keycloak, String keycloakRealm) {
        this.keycloak = keycloak;
        this.realm = keycloakRealm;
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
                .sorted(Comparator.comparing(UserDetails::name))
                .collect(Collectors.toList());
    }

    public UserDetails getUserInfo(UUID userId) {
        UserRepresentation userRepresentation;
        try {
            userRepresentation =
                    keycloak
                            .realm(realm)
                            .users()
                            .get(userId.toString())
                            .toRepresentation();
        } catch (NotFoundException ex) {
            return null;
        }
        return new UserDetails(
                UUID.fromString(userRepresentation.getId()),
                userRepresentation.getUsername()
        );
    }

    public boolean existsUser(UUID userId) {
        return getUserInfo(userId) != null;
    }
}
