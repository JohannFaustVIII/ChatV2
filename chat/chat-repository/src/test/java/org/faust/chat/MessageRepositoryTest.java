package org.faust.chat;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.faust.chat.exception.WrongOrderException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channel = UUID.randomUUID();

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

    @Test
    public void whenGetMessagesBeforeGivenAndLimitedButNotEnoughThenReturnAllMessagesBeforeGiven() {
        // given
        UUID sender = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channel = UUID.randomUUID();

        time = time.minusSeconds(10);
        List<Message> messages = new ArrayList<>(10);
        for (int i = 10; i !=0; i--) {
            messages.add(new Message(null, channel, "Sender", "Hello " + i, time, null, sender));
            time = time.plusSeconds(1);
        }

        addMessages(testedRepository,
                messages.toArray(Message[]::new)
        );

        Message beforeMessage = findMessage(testedRepository, channel, "Hello 2");
        List<Message> expectedMessages = new ArrayList<>(messages.stream().limit(8).toList());
        Collections.reverse(expectedMessages);

        // when
        Collection<Message> result = testedRepository.getAllMessages(channel, beforeMessage.id(), null, 10);

        // then
        Assertions.assertFalse(result.isEmpty());
        assertMessages(result,
                expectedMessages.toArray(Message[]::new)
        );
    }

    @Test
    public void whenGetMessagesBeforeGivenAndLimitedThenReturnLimitedMessagesAfterGiven() {
        // given
        UUID sender = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channel = UUID.randomUUID();

        time = time.minusSeconds(10);
        List<Message> messages = new ArrayList<>(10);
        for (int i = 10; i !=0; i--) {
            messages.add(new Message(null, channel, "Sender", "Hello " + i, time, null, sender));
            time = time.plusSeconds(1);
        }

        addMessages(testedRepository,
                messages.toArray(Message[]::new)
        );

        Message beforeMessage = findMessage(testedRepository, channel, "Hello 2");
        List<Message> expectedMessages = new ArrayList<>(messages.stream().skip(5).limit(3).toList());
        Collections.reverse(expectedMessages);

        // when
        Collection<Message> result = testedRepository.getAllMessages(channel, beforeMessage.id(), null, 3);

        // then
        Assertions.assertFalse(result.isEmpty());
        assertMessages(result,
                expectedMessages.toArray(Message[]::new)
        );
    }

    @Test
    public void whenGetMessagesAfterGivenAndLimitedThenReturnLimitedMessagesAfterGiven() {
        // given
        UUID sender = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channel = UUID.randomUUID();

        time = time.minusSeconds(10);
        List<Message> messages = new ArrayList<>(10);
        for (int i = 10; i !=0; i--) {
            messages.add(new Message(null, channel, "Sender", "Hello " + i, time, null, sender));
            time = time.plusSeconds(1);
        }

        addMessages(testedRepository,
                messages.toArray(Message[]::new)
        );

        Message afterMessage = findMessage(testedRepository, channel, "Hello 8");
        List<Message> expectedMessages = new ArrayList<>(messages.stream().skip(3).limit(3).toList());
        Collections.reverse(expectedMessages);

        // when
        Collection<Message> result = testedRepository.getAllMessages(channel, null, afterMessage.id(),3);

        // then
        Assertions.assertFalse(result.isEmpty());
        assertMessages(result,
                expectedMessages.toArray(Message[]::new)
        );
    }

    @Test
    public void whenGetMessagesAfterGivenAndLimitedButNotEnoughThenReturnAllMessagesAfterGiven() {
        // given
        UUID sender = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channel = UUID.randomUUID();

        time = time.minusSeconds(10);
        List<Message> messages = new ArrayList<>(10);
        for (int i = 10; i !=0; i--) {
            messages.add(new Message(null, channel, "Sender", "Hello " + i, time, null, sender));
            time = time.plusSeconds(1);
        }

        addMessages(testedRepository,
                messages.toArray(Message[]::new)
        );

        Message afterMessage = findMessage(testedRepository, channel, "Hello 8");
        List<Message> expectedMessages = new ArrayList<>(messages.stream().skip(3).limit(7).toList());
        Collections.reverse(expectedMessages);

        // when
        Collection<Message> result = testedRepository.getAllMessages(channel, null, afterMessage.id(),10);

        // then
        Assertions.assertFalse(result.isEmpty());
        assertMessages(result,
                expectedMessages.toArray(Message[]::new)
        );
    }

    @Test
    public void whenGetMessagesBetweenGivenAndLimitedThenReturnLimitedMessagesBeforeGiven() {
        // given
        UUID sender = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channel = UUID.randomUUID();

        time = time.minusSeconds(10);
        List<Message> messages = new ArrayList<>(10);
        for (int i = 10; i !=0; i--) {
            messages.add(new Message(null, channel, "Sender", "Hello " + i, time, null, sender));
            time = time.plusSeconds(1);
        }

        addMessages(testedRepository,
                messages.toArray(Message[]::new)
        );

        Message beforeMessage = findMessage(testedRepository, channel, "Hello 3");
        Message afterMessage = findMessage(testedRepository, channel, "Hello 8");
        List<Message> expectedMessages = new ArrayList<>(messages.stream().skip(5).limit(2).toList());
        Collections.reverse(expectedMessages);

        // when
        Collection<Message> result = testedRepository.getAllMessages(channel, beforeMessage.id(), afterMessage.id(),2);

        // then
        Assertions.assertFalse(result.isEmpty());
        assertMessages(result,
                expectedMessages.toArray(Message[]::new)
        );
    }

    @Test
    public void whenGetMessagesBetweenGivenAndLimitedButNotEnoughThenReturnAllMessagesBetweenGiven() {
        // given
        UUID sender = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channel = UUID.randomUUID();

        time = time.minusSeconds(10);
        List<Message> messages = new ArrayList<>(10);
        for (int i = 10; i !=0; i--) {
            messages.add(new Message(null, channel, "Sender", "Hello " + i, time, null, sender));
            time = time.plusSeconds(1);
        }

        addMessages(testedRepository,
                messages.toArray(Message[]::new)
        );

        Message beforeMessage = findMessage(testedRepository, channel, "Hello 3");
        Message afterMessage = findMessage(testedRepository, channel, "Hello 8");
        List<Message> expectedMessages = new ArrayList<>(messages.stream().skip(3).limit(4).toList());
        Collections.reverse(expectedMessages);

        // when
        Collection<Message> result = testedRepository.getAllMessages(channel, beforeMessage.id(), afterMessage.id(),10);

        // then
        Assertions.assertFalse(result.isEmpty());
        assertMessages(result,
                expectedMessages.toArray(Message[]::new)
        );
    }

    @Test
    public void whenGetMessagesBetweenIsEmptyThenEmptyCollection() {
        // given
        UUID sender = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channel = UUID.randomUUID();

        time = time.minusSeconds(10);
        List<Message> messages = new ArrayList<>(10);
        for (int i = 10; i !=0; i--) {
            messages.add(new Message(null, channel, "Sender", "Hello " + i, time, null, sender));
            time = time.plusSeconds(1);
        }

        addMessages(testedRepository,
                messages.toArray(Message[]::new)
        );

        Message beforeMessage = findMessage(testedRepository, channel, "Hello 7");
        Message afterMessage = findMessage(testedRepository, channel, "Hello 8");
        List<Message> expectedMessages = Collections.emptyList();
        Collections.reverse(expectedMessages);

        // when
        Collection<Message> result = testedRepository.getAllMessages(channel, beforeMessage.id(), afterMessage.id(),10);

        // then
        Assertions.assertTrue(result.isEmpty());
        assertMessages(result,
                expectedMessages.toArray(Message[]::new)
        );
    }

    @Test
    public void whenGetMessagesBetweenInIncorrectOrderButThenException() {
// given
        UUID sender = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channel = UUID.randomUUID();

        time = time.minusSeconds(10);
        List<Message> messages = new ArrayList<>(10);
        for (int i = 10; i !=0; i--) {
            messages.add(new Message(null, channel, "Sender", "Hello " + i, time, null, sender));
            time = time.plusSeconds(1);
        }

        addMessages(testedRepository,
                messages.toArray(Message[]::new)
        );

        Message beforeMessage = findMessage(testedRepository, channel, "Hello 10");
        Message afterMessage = findMessage(testedRepository, channel, "Hello 1");
        List<Message> expectedMessages = Collections.emptyList();
        Collections.reverse(expectedMessages);

        // when-then
        Assertions.assertThrows(WrongOrderException.class, () -> testedRepository.getAllMessages(channel, beforeMessage.id(), afterMessage.id(),10));
    }

    @Test
    public void whenAddMessageThenAdded() {
        // given
        DSLContext spyContext = Mockito.spy(context);
        MessageRepository testedRepository = new MessageRepository(spyContext);

        UUID channelId = UUID.randomUUID();
        String sender = "Random sender";
        String message = "Random message";
        UUID senderId = UUID.randomUUID();
        Message messageToAdd = new Message(
                null,
                channelId,
                sender,
                message,
                LocalDateTime.now(),
                null,
                senderId);

        // when
        testedRepository.addMessage(messageToAdd);

        // then
        Mockito.verify(spyContext).insertInto(DSL.table(DSL.name("messageTable")));
    }

    @Test
    public void whenAddMessageThenReturnedForWholeChannel() {
        // given
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channelId = UUID.randomUUID();
        String sender = "Random sender";
        String message = "Random message";
        UUID senderId = UUID.randomUUID();


        Message[] previousMessages = new Message[7];
        for (int i = 0; i != 7; i++) {
            previousMessages[i] = new Message(
                            null,
                            channelId,
                            "Sender" + i,
                            "Message" + i,
                            LocalDateTime.now(),
                            null,
                            UUID.randomUUID()
                    );

        }

        addMessages(testedRepository, previousMessages);

        Message messageToAdd = new Message(
                null,
                channelId,
                sender,
                message,
                LocalDateTime.now(),
                null,
                senderId);
        // when
        testedRepository.addMessage(messageToAdd);

        // then
        Collection<Message> allMessages = testedRepository.getAllMessages(channelId, null, null, 10);
        Iterator<Message> it = allMessages.iterator();
        Message lastMessage = it.next();

        Assertions.assertEquals(message, lastMessage.message());
        Assertions.assertEquals(channelId, lastMessage.channelId());
        Assertions.assertEquals(sender, lastMessage.sender());
        Assertions.assertEquals(senderId, lastMessage.senderId());
    }

    @Test
    public void whenAddMessageThenNotReturnedForOtherChannel() {
        // given
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channelId = UUID.randomUUID();
        String sender = "Random sender";
        String message = "Random message";
        UUID senderId = UUID.randomUUID();


        UUID otherChannel = UUID.randomUUID();
        while (otherChannel.equals(channelId)) {
            otherChannel = UUID.randomUUID();
        }

        Message[] previousMessages = new Message[7];
        for (int i = 1; i !=8 ; i++) {
            previousMessages[7 - i] = new Message(
                    null,
                    otherChannel,
                    "Sender" + i,
                    "Message" + i,
                    LocalDateTime.now(),
                    null,
                    UUID.randomUUID()
            );

        }

        addMessages(testedRepository, previousMessages);

        Message messageToAdd = new Message(
                null,
                channelId,
                sender,
                message,
                LocalDateTime.now(),
                null,
                senderId);
        // when
        testedRepository.addMessage(messageToAdd);

        // then
        Collection<Message> otherChannelMessages = testedRepository.getAllMessages(otherChannel, null, null, 10);
        assertMessages(otherChannelMessages, previousMessages);
    }

    @Test
    public void whenEditMessageThenEdited() {
        // given
        DSLContext spyContext = Mockito.spy(context);
        MessageRepository testedRepository = new MessageRepository(spyContext);

        UUID channelId = UUID.randomUUID();
        String sender = "Random sender";
        String message = "Random message";
        UUID senderId = UUID.randomUUID();
        Message messageToAdd = new Message(
                null,
                channelId,
                sender,
                message,
                LocalDateTime.now(),
                null,
                senderId);

        testedRepository.addMessage(messageToAdd);
        Message addedMessage = findMessage(testedRepository, channelId, message);
        String editMessage = "Edited message";
        // when
        testedRepository.editMessage(addedMessage.id(), editMessage, LocalDateTime.now());

        // then
        Mockito.verify(spyContext).update(DSL.table("\"messageTable\""));
        Message editedMessage = findMessage(testedRepository, channelId, editMessage);
        Assertions.assertNotNull(editedMessage);
        Assertions.assertEquals(sender, editedMessage.sender());
        Assertions.assertEquals(senderId, editedMessage.senderId());
        Assertions.assertEquals(channelId, editedMessage.channelId());
        Assertions.assertEquals(editMessage, editedMessage.message());
    }

    @Test
    public void whenEditMessageThenReturnedForWholeChannel() {
        // given
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channelId = UUID.randomUUID();

        Message[] previousMessages = new Message[7];
        for (int i = 0; i != 7; i++) {
            previousMessages[i] = new Message(
                    null,
                    channelId,
                    "Sender" + i,
                    "Message" + i,
                    LocalDateTime.now(),
                    null,
                    UUID.randomUUID()
            );

        }

        addMessages(testedRepository, previousMessages);

        Message message = findMessage(testedRepository, channelId, "Message3");
        String editMessage = "Edited message";
        // when
        testedRepository.editMessage(message.id(), editMessage, LocalDateTime.now());

        // then
        Collection<Message> allMessages = testedRepository.getAllMessages(channelId, null, null, 10);
        Iterator<Message> it = allMessages.iterator();
        it.next();
        it.next();
        it.next();

        Message editedMessage = it.next();
        Assertions.assertNotNull(editedMessage);
        Assertions.assertEquals("Sender3", editedMessage.sender());
        Assertions.assertEquals(channelId, editedMessage.channelId());
        Assertions.assertEquals(editMessage, editedMessage.message());
    }

    @Test
    public void whenEditMessageThenNotReturnedForOtherChannel() {
        // given
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channelId = UUID.randomUUID();
        String sender = "Random sender";
        String message = "Message2";
        UUID senderId = UUID.randomUUID();


        UUID otherChannel = UUID.randomUUID();
        while (otherChannel.equals(channelId)) {
            otherChannel = UUID.randomUUID();
        }

        Message[] previousMessages = new Message[7];
        for (int i = 1; i !=8 ; i++) {
            previousMessages[7 - i] = new Message(
                    null,
                    otherChannel,
                    "Sender" + i,
                    "Message" + i,
                    LocalDateTime.now(),
                    null,
                    UUID.randomUUID()
            );

        }

        addMessages(testedRepository, previousMessages);

        Message messageToAdd = new Message(
                null,
                channelId,
                sender,
                message,
                LocalDateTime.now(),
                null,
                senderId);

        testedRepository.addMessage(messageToAdd);
        Message addedMessage = findMessage(testedRepository, channelId, message);
        String editMessage = "Edited message";
        // when
        testedRepository.editMessage(addedMessage.id(), editMessage, LocalDateTime.now());

        // then
        Collection<Message> otherChannelMessages = testedRepository.getAllMessages(otherChannel, null, null, 10);
        assertMessages(otherChannelMessages, previousMessages);
    }

    @Test
    public void whenDeleteMessageThenDeleted() {
        // given
        DSLContext spyContext = Mockito.spy(context);
        MessageRepository testedRepository = new MessageRepository(spyContext);

        UUID channelId = UUID.randomUUID();
        String sender = "Random sender";
        String message = "Random message";
        UUID senderId = UUID.randomUUID();
        Message messageToAdd = new Message(
                null,
                channelId,
                sender,
                message,
                LocalDateTime.now(),
                null,
                senderId);

        testedRepository.addMessage(messageToAdd);
        Message addedMessage = findMessage(testedRepository, channelId, message);
        // when
        testedRepository.deleteMessage(addedMessage.id());

        // then
        Mockito.verify(spyContext).deleteFrom(DSL.table("\"messageTable\""));
        Assertions.assertThrows(NoSuchElementException.class, () -> findMessage(testedRepository, channelId, message));
    }

    @Test
    public void whenDeleteMessageThenNotReturnedForWholeChannel() {
        // given
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channelId = UUID.randomUUID();

        Message[] previousMessages = new Message[7];
        for (int i = 0; i != 7; i++) {
            previousMessages[i] = new Message(
                    null,
                    channelId,
                    "Sender" + i,
                    "Message" + i,
                    LocalDateTime.now(),
                    null,
                    UUID.randomUUID()
            );

        }

        addMessages(testedRepository, previousMessages);

        Message message = findMessage(testedRepository, channelId, "Message3");
        // when
        testedRepository.deleteMessage(message.id());

        // then
        Collection<Message> allMessages = testedRepository.getAllMessages(channelId, null, null, 10);

        for (Message m : allMessages) {
            Assertions.assertNotEquals("Message3", m.message());
            Assertions.assertEquals(channelId, m.channelId());
        }
    }

    @Test
    public void whenDeleteMessageThenOtherChannelKeptIntact() {
        // given
        MessageRepository testedRepository = new MessageRepository(context);

        UUID channelId = UUID.randomUUID();
        String sender = "Random sender";
        String message = "Message2";
        UUID senderId = UUID.randomUUID();


        UUID otherChannel = UUID.randomUUID();
        while (otherChannel.equals(channelId)) {
            otherChannel = UUID.randomUUID();
        }

        Message[] previousMessages = new Message[7];
        for (int i = 1; i !=8 ; i++) {
            previousMessages[7 - i] = new Message(
                    null,
                    otherChannel,
                    "Sender" + i,
                    "Message" + i,
                    LocalDateTime.now(),
                    null,
                    UUID.randomUUID()
            );

        }

        addMessages(testedRepository, previousMessages);

        Message messageToAdd = new Message(
                null,
                channelId,
                sender,
                message,
                LocalDateTime.now(),
                null,
                senderId);

        testedRepository.addMessage(messageToAdd);
        Message addedMessage = findMessage(testedRepository, channelId, message);
        // when
        testedRepository.deleteMessage(addedMessage.id());

        // then
        Collection<Message> otherChannelMessages = testedRepository.getAllMessages(otherChannel, null, null, 10);
        assertMessages(otherChannelMessages, previousMessages);
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