package org.faust.chat.channel;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;


class ChannelRepositoryTest {

    static JdbcDatabaseContainer databaseContainer;
    static DSLContext context;

    @BeforeAll
    public static void setUpDependencies() {
        databaseContainer = setUpDatabaseContainer();
        context = setUpContext(databaseContainer);
    }

    @BeforeEach
    public void clearDb() {
        context.deleteFrom(DSL.table(DSL.name("channelTable"))).execute();
    }

    @Test
    public void whenAddingChannelThenReturnedWithTheRest() {

    }

    @Test
    public void whenAddingChannelThenExists() {

    }

    @Test
    public void whenExistsChannelWithGivenNameThenReturnTrue() {
        // given
        ChannelRepository channelRepository = new ChannelRepository(context);

        channelRepository.addChannel(new Channel(null, "C1"));
        // when
        boolean result = channelRepository.existsChannelWithName("C1");
        // then
        Assertions.assertTrue(result);
    }

    @Test
    public void whenNotExistsChannelWithGivenNameThenReturnFalse() {
        // given
        ChannelRepository channelRepository = new ChannelRepository(context);
        // when
        boolean result = channelRepository.existsChannelWithName("Not Existing Channel");
        // then
        Assertions.assertFalse(result);
    }

    @Test
    public void whenExistsChannelWithGivenIdThenReturnTrue() {
        // given
        ChannelRepository channelRepository = new ChannelRepository(context);

        channelRepository.addChannel(new Channel(null, "C1"));
        UUID idToFind = channelRepository.getAllChannels().iterator().next().id();
        // when
        boolean result = channelRepository.existsChannelWithId(idToFind);
        // then
        Assertions.assertTrue(result);
    }

    @Test
    public void whenNotExistsChannelWithGivenIdThenReturnFalse() {
        // given
        ChannelRepository channelRepository = new ChannelRepository(context);

        UUID idToFind = UUID.randomUUID();
        // when
        boolean result = channelRepository.existsChannelWithId(idToFind);
        // then
        Assertions.assertFalse(result);
    }

    @Test
    public void whenNoChannelsExistThenNoChannelsReturned() {
        // given
        ChannelRepository channelRepository = new ChannelRepository(context);
        // when
        Collection<Channel> result = channelRepository.getAllChannels();
        // then
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void whenChannelsExistThenAllCanBeReturned() {
        // given
        ChannelRepository channelRepository = new ChannelRepository(context);

        channelRepository.addChannel(new Channel(null, "C1"));
        channelRepository.addChannel(new Channel(null, "Channel Second"));
        channelRepository.addChannel(new Channel(null, "Third Random Channel"));
        // when
        Collection<Channel> result = channelRepository.getAllChannels();
        // then
        Assertions.assertFalse(result.isEmpty());
        Iterator<Channel> it = result.iterator();
        Assertions.assertEquals("C1", it.next().name());
        Assertions.assertEquals("Channel Second", it.next().name());
        Assertions.assertEquals("Third Random Channel", it.next().name());
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