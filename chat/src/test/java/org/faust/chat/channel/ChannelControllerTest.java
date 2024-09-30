package org.faust.chat.channel;

import org.faust.chat.Main;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Main.class
)
@AutoConfigureMockMvc
@TestPropertySource(
        properties = {
                "spring.datasource.url=${TEST_PG_URL}",
                "spring.datasource.username=${TEST_PG_USERNAME}",
                "spring.datasource.password=${TEST_PG_PASSWORD}",
                "keycloak.url=${KEYCLOAK_URL}",
                "keycloak.realm=${KEYCLOAK_REALM}",
                "keycloak.clientId=${KEYCLOAK_CLIENT_ID}",
                "keycloak.clientSecret=${KEYCLOAK_CLIENT_SECRET}"
        }
)
class ChannelControllerTest { // TODO: FIX LIQUIBASE DEPENDENCY; and move to some base?
    private static JdbcDatabaseContainer databaseContainer;
    private static GenericContainer<?> keycloakContainer;

    private static String KEYCLOAK_REALM = "chatTestRealm";
    private static String KEYCLOAK_ID = "test-id";
    private static String KEYCLOAK_SECRET = "test-id-secret";

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    public static void setUp() {
        databaseContainer = setUpDatabase();
        keycloakContainer = setUpKeycloak();
    }

    @Test
    public void test() {

    }

    @AfterAll
    public static void tearDown() {
        if (databaseContainer != null) {
            databaseContainer.close();
        }
    }

    private static JdbcDatabaseContainer setUpDatabase() {
        JdbcDatabaseContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");
        postgreSQLContainer.start();

        System.setProperty("TEST_PG_URL", postgreSQLContainer.getJdbcUrl());
        System.setProperty("TEST_PG_USERNAME", postgreSQLContainer.getUsername());
        System.setProperty("TEST_PG_PASSWORD", postgreSQLContainer.getPassword());

        return postgreSQLContainer;
    }

    private static GenericContainer<?> setUpKeycloak() {
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("quay.io/keycloak/keycloak"))
                .withExposedPorts(8080)
                .withEnv("KEYCLOAK_ADMIN", "admin")
                .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
                .waitingFor(Wait.forHttp("/").forStatusCode(200).withStartupTimeout(Duration.ofMinutes(5)))
                .withCommand("start-dev");

        container.start();

        try(Keycloak k = KeycloakBuilder.builder()
                .serverUrl("http://" + container.getHost() + ":" + container.getFirstMappedPort())
                .realm("master")
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .build()) {

            createRealm(k);
            createClient(k);

            // TODO: add users to realm
        }

        System.setProperty("KEYCLOAK_URL", "http://" + container.getHost() + ":" + container.getFirstMappedPort());
        System.setProperty("KEYCLOAK_REALM", KEYCLOAK_REALM); // Replace with your actual realm
        System.setProperty("KEYCLOAK_CLIENT_ID", KEYCLOAK_ID); // Replace with your client ID
        System.setProperty("KEYCLOAK_CLIENT_SECRET", KEYCLOAK_SECRET); // Replace with your client secret

        return container;
    }

    private static void createClient(Keycloak k) {
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(KEYCLOAK_ID);
        client.setEnabled(true);
        client.setSecret(KEYCLOAK_SECRET);
        client.setProtocol("openid-connect");
        client.setRedirectUris(Collections.singletonList("*"));

        k.realm(KEYCLOAK_REALM).clients().create(client).close();
    }

    private static void createRealm(Keycloak k) {
        RealmRepresentation newRealm = new RealmRepresentation();
        newRealm.setRealm(KEYCLOAK_REALM);
        newRealm.setEnabled(true);

        k.realms().create(newRealm);
    }
}