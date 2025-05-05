package org.faust.chat;

import base.E2ETestBase;
import base.E2ETestExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.faust.chat.command.AddMessage;
import org.faust.chat.command.CommandSerializer;
import org.faust.chat.command.EditMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

// TODO: there is endpoint to get all messages, and via kafka to edit data; so put data via kafka and then check existence? clean db between calls
// more tests may be needed as in some cases, messages are sent to SSE
@RunWith(SpringRunner.class)
@ExtendWith(E2ETestExtension.class)
@Import(MessageControllerTest.KafkaConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {ChatRepositoryApplication.class, MessageControllerTest.KafkaConfiguration.class}
)
@AutoConfigureMockMvc
@Testcontainers
class MessageControllerTest extends E2ETestBase {

    @Container
    public static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("apache/kafka"));

    @Autowired
    private MockMvc mockMvc;

    KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(KafkaConfiguration.greetingProducerFactory());

    @BeforeEach
    @AfterEach
    public void cleanDb() throws SQLException {
        try (Connection connection = databaseContainer.createConnection("")) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM \"messageTable\"");
        }
    }

    @Test
    public void givenMessageAddedWhenGettingMessagesThenReturned() throws Exception {
        // given
        UUID tokenId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "Random sender";
        String message = "Random message";
        LocalDateTime time = LocalDateTime.now();

        kafkaTemplate.send("CHAT_COMMAND", new AddMessage(tokenId, channelId, senderName, senderId, message, time));

        sleep(3000);
        // when
        ResultActions act = mockMvc.perform(MockMvcRequestBuilders.get("/chat/" + channelId));
        Collection<Message> result = readContent(act.andReturn().getResponse().getContentAsByteArray());

        // then
        Assertions.assertFalse(result.isEmpty());
        Message resultMessage = result.iterator().next();
        Assertions.assertEquals(channelId, resultMessage.channelId());
        Assertions.assertEquals(senderName, resultMessage.sender());
        Assertions.assertEquals(senderId, resultMessage.senderId());
        Assertions.assertEquals(message, resultMessage.message());
    }

    @Test
    public void givenMessageAddedWhenGettingSingleMessageThenReturned() throws Exception {
        // given
        UUID tokenId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "Random sender";
        String message = "Random message";
        LocalDateTime time = LocalDateTime.now();

        kafkaTemplate.send("CHAT_COMMAND", new AddMessage(tokenId, channelId, senderName, senderId, message, time));

        sleep(3000);
        // when
        ResultActions act = mockMvc.perform(MockMvcRequestBuilders.get("/chat/" + channelId));
        Collection<Message> result = readContent(act.andReturn().getResponse().getContentAsByteArray());
        UUID messageId = result.iterator().next().id();

        act = mockMvc.perform(MockMvcRequestBuilders.get("/chat/message/" + messageId));
        Message resultMessage = readSingleMessage(act.andReturn().getResponse().getContentAsByteArray());

        // then
        Assertions.assertEquals(channelId, resultMessage.channelId());
        Assertions.assertEquals(senderName, resultMessage.sender());
        Assertions.assertEquals(senderId, resultMessage.senderId());
        Assertions.assertEquals(message, resultMessage.message());
    }

    @Test
    public void givenMessageAddedAndEditedWhenGettingMessagesThenReturned() throws Exception {
        // TODO: is it fine? tests look really long
        // given
        UUID tokenId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "Random sender";
        String message = "Random message";
        LocalDateTime time = LocalDateTime.now();

        kafkaTemplate.send("CHAT_COMMAND", new AddMessage(tokenId, channelId, senderName, senderId, message, time));

        sleep(3000);
        ResultActions givenRetrieve = mockMvc.perform(MockMvcRequestBuilders.get("/chat/" + channelId));
        UUID messageId =  readContent(givenRetrieve.andReturn().getResponse().getContentAsByteArray()).iterator().next().id();

        String editedMessage = "Edited message";
        kafkaTemplate.send("CHAT_COMMAND", new EditMessage(tokenId, channelId, messageId, senderId, editedMessage, time.plusMinutes(1)));

        sleep(3000);

        // when
        ResultActions act = mockMvc.perform(MockMvcRequestBuilders.get("/chat/" + channelId));
        Collection<Message> result = readContent(act.andReturn().getResponse().getContentAsByteArray());

        // then
        Assertions.assertFalse(result.isEmpty());
        Message resultMessage = result.iterator().next();
        Assertions.assertEquals(channelId, resultMessage.channelId());
        Assertions.assertEquals(senderName, resultMessage.sender());
        Assertions.assertEquals(senderId, resultMessage.senderId());
        Assertions.assertEquals(editedMessage, resultMessage.message());
    }

    @Test
    public void givenMessageAddedAndEditedWhenGettingSingleMessageThenReturned() throws Exception {
        // given
        UUID tokenId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        String senderName = "Random sender";
        String message = "Random message";
        LocalDateTime time = LocalDateTime.now();

        kafkaTemplate.send("CHAT_COMMAND", new AddMessage(tokenId, channelId, senderName, senderId, message, time));

        sleep(3000);
        ResultActions givenRetrieve = mockMvc.perform(MockMvcRequestBuilders.get("/chat/" + channelId));
        UUID messageId =  readContent(givenRetrieve.andReturn().getResponse().getContentAsByteArray()).iterator().next().id();

        String editedMessage = "Edited message";
        kafkaTemplate.send("CHAT_COMMAND", new EditMessage(tokenId, channelId, messageId, senderId, editedMessage, time.plusMinutes(1)));

        sleep(3000);
        // when

        ResultActions act = mockMvc.perform(MockMvcRequestBuilders.get("/chat/message/" + messageId));
        Message resultMessage = readSingleMessage(act.andReturn().getResponse().getContentAsByteArray());

        // then
        Assertions.assertEquals(channelId, resultMessage.channelId());
        Assertions.assertEquals(senderName, resultMessage.sender());
        Assertions.assertEquals(senderId, resultMessage.senderId());
        Assertions.assertEquals(editedMessage, resultMessage.message());
    }

    @Test
    public void givenMessageAddedAndDeletedWhenGettingMessagesThenNotReturned() {

    }

    @Test
    public void givenMessageAddedAndDeletedWhenGettingSingleMessageThenNotReturned() {

    }

    @Test
    public void givenMessageAddedWhenGettingMessagesFromOtherChannelThenNotReturned() {

    }

    @Test
    public void givenMessageAddedAndEditedWhenGettingMessagesFromOtherChannelThenNotReturned() {

    }

    @Test
    public void givenMessageAddedAndDeletedWhenGettingMessagesFromOtherChannelThenNotReturned() {

    }

    @Test
    public void whenEditingNotExistingMessageThenSSEError() {

    }

    @Test
    public void whenEditingMessageInWrongChannelThenSSEError() {

    }

    @Test
    public void whenEditingMessageByNotPermittedUserThenSSEError() {

    }

    @Test
    public void whenDeletingNotExistingMessageThenSSEError() {

    }

    @Test
    public void whenDeletingMessageInWrongChannelThenSSEError() {

    }

    @Test
    public void whenDeletingMessageByNotPermittedUserThenSSEError() {

    }

    private static Collection<Message> readContent(byte[] data) throws IOException {
        CollectionType type = new ObjectMapper().getTypeFactory().constructCollectionType(List.class, Message.class);
        return new ObjectMapper().readValue(data, type);
    }

    private static Message readSingleMessage(byte[] data) throws IOException {
        return new ObjectMapper().readValue(data, Message.class);
    }

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrapServers", kafka::getBootstrapServers);
    }

    static class KafkaConfiguration {
        @Bean
        public ConsumerFactory<Object, Object> producerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class);
            configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.faust.chat.command.CommandDeserializer.class);
            configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-test");
            configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            // more standard configuration
            return new DefaultKafkaConsumerFactory<>(configProps);
        }

        public static ProducerFactory<String, Object> greetingProducerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CommandSerializer.class);
            return new DefaultKafkaProducerFactory<>(configProps);
        }
    }
}