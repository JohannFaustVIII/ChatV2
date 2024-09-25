package org.faust.chat.keycloak;

import jakarta.ws.rs.core.Response;
import org.faust.chat.user.UserDetails;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class KeycloakRepositoryTest {

    // TODO: what does it require?
    // repository is read only, so requires a testcontainer with set up users
    // the best would be to set up empty container
    // and put realms and users

    private static GenericContainer<?> keycloakContainer;
    private static Keycloak keycloak;
    private static String realm;

    @BeforeAll
    public static void setUp() {
        keycloakContainer = setUpKeycloakContainer();
        realm = "test-realm";
        keycloak = setUpKeycloak();

    }

    private static GenericContainer<?> setUpKeycloakContainer() {
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("quay.io/keycloak/keycloak"))
                .withExposedPorts(8080)
                .withEnv("KEYCLOAK_ADMIN", "admin")
                .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
                .waitingFor(Wait.forHttp("/").forStatusCode(200).withStartupTimeout(Duration.ofMinutes(5)))
                .withCommand("start-dev");

        container.start();

        return container;
    }

    private static Keycloak setUpKeycloak() {
        Keycloak k = KeycloakBuilder.builder()
                .serverUrl("http://" + keycloakContainer.getHost() + ":" + keycloakContainer.getFirstMappedPort())
                .realm("master")
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .build();

        RealmRepresentation newRealm = new RealmRepresentation();
        newRealm.setRealm(realm);
        newRealm.setEnabled(true);

        k.realms().create(newRealm);

        addUserToRealm(k, "user1");

        return k;
    }

    private static void addUserToRealm(Keycloak keycloak, String username) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setFirstName("New");
        user.setLastName("User");
        user.setEmail(username + "@example.com");
        user.setEnabled(true);

        CredentialRepresentation password = new CredentialRepresentation();
        password.setTemporary(false);
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue("user-password");

        user.setCredentials(Collections.singletonList(password));

        Response response = keycloak.realm(realm)
                .users()
                .create(user);

        response.close();
    }

    @Test
    public void whenNoUsersThenEmptyCollectionReturned() {

    }

    @Test
    public void whenUsersExistThenAllReturned() {
        // given
        KeycloakRepository testedRepository = new KeycloakRepository(keycloak, realm);
        // when
        Collection<UserDetails> result = testedRepository.getUsers();
        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("user1", result.iterator().next().name());
    }

    @Test
    public void whenUserExistsThenDetailsReturned() {

    }

    @Test
    public void whenUserNotExistsThenNull() {

    }

    @Test
    public void whenUserExistsThenTrue() {

    }

    @Test
    public void whenUserNotExistsThenFalse() {

    }

    @AfterAll
    public static void tearDown() {
        if (keycloakContainer != null) {
            keycloakContainer.stop();
        }
    }

}