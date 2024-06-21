package org.faust.chat.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class KeycloakRepository {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public KeycloakRepository(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void getUsers() {
        keycloak
                .realm(realm)
                .users()
                .list()
                .forEach(
                        u -> {
                            System.out.println(u.getId());
                            System.out.println(u.getUsername());
                        }
                );
    }
}
