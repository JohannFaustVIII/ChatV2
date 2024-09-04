package org.faust.chat.chat;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.faust.chat.channel.Channel;
import org.faust.chat.channel.ChannelRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Iterator;
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

    @BeforeEach
    public void clearDb() {
        context.deleteFrom(DSL.table(DSL.name("messageTable"))).execute();
        context.deleteFrom(DSL.table(DSL.name("channelTable"))).execute();
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

    @Test
    public void whenGetExistingMessagesThenReturnAll() {
        // given
        UUID sender = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();
        ChannelRepository channelRepository = new ChannelRepository(context);
        MessageRepository testedRepository = new MessageRepository(context);

        addChannels(channelRepository,
                new Channel(null, "Test channel")
        );

        Collection<Channel> channels = channelRepository.getAllChannels();
        UUID channel = channels.iterator().next().id();

        addMessages(testedRepository,
                new Message(null, channel, "Sender", "Hello 1", time, null, sender)
        );
        // when
        Collection<Message> result = testedRepository.getAllMessages(channel, null, null, 10);

        // then
        Assertions.assertFalse(result.isEmpty());
        assertMessages(result,
                new Message(null, channel, "Sender", "Hello 1", time, null, sender)
        );
    }

    // TODO: next tests requires handling id AND cleaning the db, think how to handle nicely

    @Test
    public void whenGetMessagesBeforeGivenAndLimitedButNotEnoughThenReturnAllMessagesBeforeGiven() {

    }

    @Test
    public void whenGetMessagesAfterGivenAndLimitedThenReturnLimitedMessagesAfterGiven() {

    }

    @Test
    public void whenGetMessagesAfterGivenAndLimitedButNotEnoughThenReturnAllMessagesAfterGiven() {

    }

    @Test
    public void whenGetMessagesBetweenGivenAndLimitedThenReturnLimitedMessagesBeforeGiven() {

    }

    @Test
    public void whenGetMessagesBetweenGivenAndLimitedButNotEnoughThenReturnAllMessagesBetweenGiven() {

    }

    @Test
    public void whenGetMessagesBetweenIsEmptyThenEmptyCollection() {

    }

    @Test
    public void whenGetMessagesBetweenInIncorrectOrderButThenException() {

    }

    private void addChannels(ChannelRepository channelRepository, Channel... channels) {
        for (Channel c : channels) {
            channelRepository.addChannel(c);
        }
    }

    private void addMessages(MessageRepository messageRepository, Message... messages) {
        for (Message m : messages) {
            messageRepository.addMessage(m);
        }
    }

    private Message findMessage(MessageRepository messageRepository, UUID channel, String messageText) {
        return messageRepository
                .getAllMessages(channel, null, null, Integer.MAX_VALUE)
                .stream()
                .filter(m -> m.message().equals(messageText))
                .findFirst()
                .orElseThrow();
    }

    private void assertMessages(Collection<Message> resultMessages, Message... expectedMessages) {
        assertEquals(expectedMessages.length, resultMessages.size());
        Iterator<Message> resultIterator = resultMessages.iterator();
        for (int i = 0; i != resultMessages.size(); i++) {
            Message result = resultIterator.next();
            Message expected = expectedMessages[i];
            assertEquals(expected.message(), result.message());
            assertEquals(expected.channelId(), result.channelId());
            assertEquals(expected.senderId(), result.senderId());
            assertEquals(expected.sender(), result.sender());
            assertEquals(expected.serverTime().truncatedTo(ChronoUnit.SECONDS), result.serverTime().truncatedTo(ChronoUnit.SECONDS));
            if (expected.editTime() == null) {
                assertNull(result.editTime());
            } else {
                assertEquals(expected.editTime().truncatedTo(ChronoUnit.SECONDS), result.editTime().truncatedTo(ChronoUnit.SECONDS));
            }
        }
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