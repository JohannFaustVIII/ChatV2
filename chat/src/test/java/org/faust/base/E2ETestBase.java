package org.faust.base;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.*;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;

@TestPropertySource(
        properties = {
                "spring.datasource.url=${TEST_PG_URL}",
                "spring.datasource.username=${TEST_PG_USERNAME}",
                "spring.datasource.password=${TEST_PG_PASSWORD}",
                "keycloak.url=${KEYCLOAK_URL}",
                "keycloak.realm=${KEYCLOAK_REALM}",
                "keycloak.clientId=${KEYCLOAK_CLIENT_ID}",
                "keycloak.clientSecret=${KEYCLOAK_CLIENT_SECRET}",
                "spring.security.oauth2.resource-server.jwt.jwk-set-uri=${JWT_PROVIDER_URI}",
                "spring.liquibase.changeLog=classpath:/db/changeLog/dbchangelog.xml"
        }
)
public abstract class E2ETestBase {

    protected static JdbcDatabaseContainer databaseContainer;
    protected static GenericContainer<?> keycloakContainer;

    protected static String KEYCLOAK_REALM = "chatTestRealm";
    protected static String KEYCLOAK_ID = "test-id";
    protected static String KEYCLOAK_SECRET = "test-id-secret";
    protected static String KEYCLOAK_USER = "testUser";
    protected static String KEYCLOAK_ACCESS_ROLE = "chat_access";
    @BeforeAll
    public static void setUp() {
        databaseContainer = setUpDatabase();
        keycloakContainer = setUpKeycloak();
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
            createAccessRole(k);
            createUser(k);
        }

        System.setProperty("KEYCLOAK_URL", "http://" + container.getHost() + ":" + container.getFirstMappedPort());
        System.setProperty("KEYCLOAK_REALM", KEYCLOAK_REALM);
        System.setProperty("KEYCLOAK_CLIENT_ID", KEYCLOAK_ID);
        System.setProperty("KEYCLOAK_CLIENT_SECRET", KEYCLOAK_SECRET);
        System.setProperty("JWT_PROVIDER_URI", "http://" + container.getHost() + ":" + container.getFirstMappedPort() + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/certs");

        return container;
    }

    private static void createRealm(Keycloak k) {
        RealmRepresentation newRealm = new RealmRepresentation();
        newRealm.setRealm(KEYCLOAK_REALM);
        newRealm.setEnabled(true);

        k.realms().create(newRealm);
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

    private static void createAccessRole(Keycloak k) {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(KEYCLOAK_ACCESS_ROLE);

        k.realm(KEYCLOAK_REALM).roles().create(role);
    }

    private static void createUser(Keycloak k) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(KEYCLOAK_USER);
        user.setFirstName("New");
        user.setLastName("User");
        user.setEmail(KEYCLOAK_USER + "@example.com");
        user.setEnabled(true);

        CredentialRepresentation password = new CredentialRepresentation();
        password.setTemporary(false);
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue("password");

        user.setCredentials(Collections.singletonList(password));

        String userId = k.realm(KEYCLOAK_REALM)
                .users()
                .create(user).getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");;

        RoleRepresentation role = k.realm(KEYCLOAK_REALM).roles().get(KEYCLOAK_ACCESS_ROLE).toRepresentation(); // Replace with your role name
        k.realm(KEYCLOAK_REALM).users().get(userId).roles().realmLevel().add(Collections.singletonList(role));
    }

    public static String getAuthorizationToken() {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl("http://" + keycloakContainer.getHost() + ":" + keycloakContainer.getFirstMappedPort())
                .realm(KEYCLOAK_REALM)
                .clientId(KEYCLOAK_ID)
                .clientSecret(KEYCLOAK_SECRET)
                .username(KEYCLOAK_USER)
                .password("password")
                .grantType(OAuth2Constants.PASSWORD)
                .build();

        return "Bearer " + keycloak.tokenManager().getAccessToken().getToken();
    }
}
