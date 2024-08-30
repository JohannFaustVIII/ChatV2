package org.faust.chat.chat;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class MessageRepositoryTest {

    static JdbcDatabaseContainer databaseContainer;
    static DSLContext context;

    @BeforeAll
    public static void setUpDependencies() {
        databaseContainer = setUpDatabaseContainer();
        context = setUpContext(databaseContainer);
    }

    @Test
    public void whenGetMessagesThenReturnEmptyCollection() {
        // given
        UUID channel = UUID.randomUUID();
        MessageRepository testedRepository = new MessageRepository(context);

        // when
        Collection<Message> result = testedRepository.getAllMessages(channel, null, null, 10);

        // then
        Assertions.assertTrue(result.isEmpty());
    }

    @AfterAll
    public static void closeDependencies() {
        databaseContainer.close();
    }

    private static JdbcDatabaseContainer setUpDatabaseContainer() {
        JdbcDatabaseContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");
        postgreSQLContainer.start();

        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        String username = postgreSQLContainer.getUsername();
        String password = postgreSQLContainer.getPassword();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/changeLog/dbchangelog.xml", new ClassLoaderResourceAccessor(), database);

            liquibase.update("");

        } catch (SQLException | LiquibaseException e) {
            e.printStackTrace();
            return null;
        }
        return postgreSQLContainer;
    }

    private static DSLContext setUpContext(JdbcDatabaseContainer databaseContainer) {
        String jdbcUrl = databaseContainer.getJdbcUrl();
        String username = databaseContainer.getUsername();
        String password = databaseContainer.getPassword();

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            return DSL.using(connection, SQLDialect.POSTGRES);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

}