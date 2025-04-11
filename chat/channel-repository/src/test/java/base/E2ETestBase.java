package base;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.UUID;

@TestPropertySource(
        properties = {
                "spring.datasource.url=${TEST_PG_URL}",
                "spring.datasource.username=${TEST_PG_USERNAME}",
                "spring.datasource.password=${TEST_PG_PASSWORD}",
                "spring.liquibase.changeLog=classpath:/db/changeLog/dbchangelog.xml"
        }
)
public abstract class E2ETestBase {

    protected static JdbcDatabaseContainer databaseContainer;

    public static void setUp() {
        databaseContainer = setUpDatabase();
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
    }

    private static JdbcDatabaseContainer setUpDatabase() {
        JdbcDatabaseContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");
        postgreSQLContainer.start();

        return postgreSQLContainer;
    }
}
