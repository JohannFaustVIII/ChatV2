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
import java.util.UUID;

class KeycloakRepositoryTest {

    private static GenericContainer<?> keycloakContainer;
    private static Keycloak keycloak;
    private static String realm;
    private static String emptyRealm;

    @BeforeAll
    public static void setUp() {
        keycloakContainer = setUpKeycloakContainer();
        realm = "testRealm";
        emptyRealm = "emptyRealm";
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

        createRealm(realm, k);
        createRealm(emptyRealm, k);

        addUserToRealm(k, "user1");

        return k;
    }

    private static void createRealm(String realm, Keycloak k) {
        RealmRepresentation newRealm = new RealmRepresentation();
        newRealm.setRealm(realm);
        newRealm.setEnabled(true);

        k.realms().create(newRealm);
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
        // given
        KeycloakRepository testedRepository = new KeycloakRepository(keycloak, emptyRealm);
        // when
        Collection<UserDetails> result = testedRepository.getUsers();
        // then
        Assertions.assertTrue(result.isEmpty());
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
        // given
        KeycloakRepository testedRepository = new KeycloakRepository(keycloak, realm);
        UUID userId = testedRepository.getUsers().iterator().next().id();
        // when
        UserDetails result = testedRepository.getUserInfo(userId);
        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("user1", result.name());
    }

    @Test
    public void whenUserNotExistsThenNull() {
        // given
        KeycloakRepository testedRepository = new KeycloakRepository(keycloak, realm);
        // when
        UserDetails result = testedRepository.getUserInfo(UUID.randomUUID());
        // then
        Assertions.assertNull(result);
    }

    @Test
    public void whenUserExistsThenTrue() {
        // given
        KeycloakRepository testedRepository = new KeycloakRepository(keycloak, realm);
        UUID userId = testedRepository.getUsers().iterator().next().id();
        // when
        boolean result = testedRepository.existsUser(userId);
        // then
        Assertions.assertTrue(result);
    }

    @Test
    public void whenUserNotExistsThenFalse() {
        // given
        KeycloakRepository testedRepository = new KeycloakRepository(keycloak, realm);
        // when
        boolean result = testedRepository.existsUser(UUID.randomUUID());
        // then
        Assertions.assertFalse(result);
    }

    @AfterAll
    public static void tearDown() {
        if (keycloakContainer != null) {
            keycloakContainer.stop();
        }
    }

}