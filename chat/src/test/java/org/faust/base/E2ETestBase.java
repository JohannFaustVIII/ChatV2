package org.faust.base;

import org.jetbrains.annotations.NotNull;
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
import java.util.List;
import java.util.stream.Collectors;

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

    public static void setUp() {
        databaseContainer = setUpDatabase();
        keycloakContainer = setUpKeycloak();
    }

    public static void tearDown() {
        if (databaseContainer != null) {
            databaseContainer.close();
        }
    }

    @BeforeAll
    public static void setUpProperties() {
        if (databaseContainer != null) {
            System.setProperty("TEST_PG_URL", databaseContainer.getJdbcUrl());
            System.setProperty("TEST_PG_USERNAME", databaseContainer.getUsername());
            System.setProperty("TEST_PG_PASSWORD", databaseContainer.getPassword());
        }
        if (keycloakContainer != null) {
            System.setProperty("KEYCLOAK_URL", "http://" + keycloakContainer.getHost() + ":" + keycloakContainer.getFirstMappedPort());
            System.setProperty("KEYCLOAK_REALM", KEYCLOAK_REALM);
            System.setProperty("KEYCLOAK_CLIENT_ID", KEYCLOAK_ID);
            System.setProperty("KEYCLOAK_CLIENT_SECRET", KEYCLOAK_SECRET);
            System.setProperty("JWT_PROVIDER_URI", "http://" + keycloakContainer.getHost() + ":" + keycloakContainer.getFirstMappedPort() + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/certs");
        }
    }

    private static JdbcDatabaseContainer setUpDatabase() {
        JdbcDatabaseContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");
        postgreSQLContainer.start();

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
            setUpClient(k);
            createAccessRole(k);
            createUser(k);
        }

        return container;
    }

    private static void createRealm(Keycloak k) {
        RealmRepresentation newRealm = new RealmRepresentation();
        newRealm.setRealm(KEYCLOAK_REALM);
        newRealm.setEnabled(true);

        k.realms().create(newRealm);
    }

    private static void setUpClient(Keycloak k) {
        createClient(k);

        String clientId = k.realm(KEYCLOAK_REALM).clients().findByClientId(KEYCLOAK_ID).get(0).getId();

        String scopeId = createClientScope(k);

        String realmManagementClientId = getRealmManagementClientId(k);
        List<RoleRepresentation> roles = getManagementRoles(k, realmManagementClientId);

        addRolesToClientScope(k, scopeId, roles);

        addClientScopeToClient(k, clientId, scopeId);
        addRolesToClientServiceAccount(k, clientId, realmManagementClientId, roles);
    }

    private static void createClient(Keycloak k) {
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(KEYCLOAK_ID);
        client.setEnabled(true);
        client.setSecret(KEYCLOAK_SECRET);
        client.setPublicClient(false);
        client.setDirectAccessGrantsEnabled(true);
        client.setServiceAccountsEnabled(true);
        client.setAuthorizationServicesEnabled(true);
        client.setProtocol("openid-connect");
        client.setRedirectUris(Collections.singletonList("*"));

        k.realm(KEYCLOAK_REALM).clients().create(client).close();
    }

    private static String createClientScope(Keycloak k) {
        ClientScopeRepresentation clientScope = new ClientScopeRepresentation();
        clientScope.setName("management");
        clientScope.setProtocol("openid-connect");

        k.realm(KEYCLOAK_REALM).clientScopes().create(clientScope).close();

        String scopeId = k.realm(KEYCLOAK_REALM).clientScopes().findAll().stream().filter(c -> c.getName().equals("management")).findFirst().get().getId();
        return scopeId;
    }

    private static String getRealmManagementClientId(Keycloak k) {
        String realmManagementClientId = k.realm(KEYCLOAK_REALM)
                .clients()
                .findByClientId("realm-management")
                .get(0)
                .getId();
        return realmManagementClientId;
    }

    @NotNull
    private static List<RoleRepresentation> getManagementRoles(Keycloak k, String realmManagementClientId) {
        List<RoleRepresentation> roles = k.realm(KEYCLOAK_REALM)
                .clients()
                .get(realmManagementClientId)
                .roles()
                .list()
                .stream()
                .filter(
                        r -> r.getName().equals("view-users")
                                || r.getName().equals("manage-users")
                                || r.getName().equals("view-clients")
                                || r.getName().equals("view-realm")
                ).collect(Collectors.toList());
        return roles;
    }

    private static void addRolesToClientScope(Keycloak k, String scopeId, List<RoleRepresentation> roles) {
        k.realm(KEYCLOAK_REALM).clientScopes().get(scopeId).getScopeMappings().realmLevel().add(
                roles
        );
    }

    private static void addClientScopeToClient(Keycloak k, String clientId, String scopeId) {
        k.realm(KEYCLOAK_REALM).clients().get(clientId).addDefaultClientScope(scopeId);
    }

    private static void addRolesToClientServiceAccount(Keycloak k, String clientId, String realmManagementClientId, List<RoleRepresentation> roles) {
        String serviceUserId = k.realm(KEYCLOAK_REALM).clients().get(clientId).getServiceAccountUser().getId();
        k.realm(KEYCLOAK_REALM).users().get(serviceUserId).roles().clientLevel(realmManagementClientId).add(roles);
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

        RoleRepresentation role = k.realm(KEYCLOAK_REALM).roles().get(KEYCLOAK_ACCESS_ROLE).toRepresentation();
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
