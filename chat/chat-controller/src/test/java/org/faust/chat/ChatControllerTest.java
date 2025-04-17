package org.faust.chat;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.faust.chat.command.AddMessage;
import org.faust.chat.command.CommandDeserializer;
import org.faust.chat.command.DeleteMessage;
import org.faust.chat.command.EditMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@Import(ChatControllerTest.KafkaConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {ChatControllerApplication.class, ChatControllerTest.KafkaConfiguration.class}
)
@AutoConfigureMockMvc
@Testcontainers
class ChatControllerTest {

    @Container
    public static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("apache/kafka"));

    public static KafkaConsumer<String, Object> kafkaConsumer;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void defineKafkaConsumer() {
        kafkaConsumer = new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "collector-test",
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"),
                new StringDeserializer(),
                new CommandDeserializer());
        kafkaConsumer.subscribe(Collections.singletonList("CHAT_REQUEST"));
    }

    @Test
    public void whenAddingMessageThenItAppearsInKafka() throws Exception {
        // given
        UUID userToken = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        String message = "Initial message";
        String username = "Test user";
        UUID userId = UUID.randomUUID();


        // when
        ResultActions act = mockMvc.perform(post("/chat/" + channelId.toString())
                .header("GW_TOKEN_ID", userToken.toString())
                .header("GW_USER_ID", userId.toString())
                .header("GW_USER", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(message));
        act.andExpect(status().isOk());

        // then
        ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMinutes(1));

        Assertions.assertFalse(records.isEmpty());

        int count = 0;

        for (ConsumerRecord<String, Object> record : records) {
            AddMessage addMessage = (AddMessage) record.value();

            Assertions.assertEquals("Initial message" , addMessage.message());
            Assertions.assertEquals(userToken, addMessage.tokenId());
            Assertions.assertEquals(userId, addMessage.senderId());
            Assertions.assertEquals(channelId, addMessage.channel());
            Assertions.assertEquals("Test user", addMessage.sender());
            count++;
        }

        Assertions.assertEquals(1, count);
    }

    @Test
    public void whenEditingMessageThenItAppearsInKafka() throws Exception {
        // given
        UUID userToken = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        String message = "Edited message";
        UUID userId = UUID.randomUUID();


        // when
        ResultActions act = mockMvc.perform(put("/chat/" + channelId + "/" + messageId)
                .header("GW_TOKEN_ID", userToken.toString())
                .header("GW_USER_ID", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(message));
        act.andExpect(status().isOk());

        // then
        ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMinutes(1));

        Assertions.assertFalse(records.isEmpty());

        int count = 0;

        for (ConsumerRecord<String, Object> record : records) {
            EditMessage editMessage = (EditMessage) record.value();

            Assertions.assertEquals("Edited message" , editMessage.newMessage());
            Assertions.assertEquals(userToken, editMessage.tokenId());
            Assertions.assertEquals(userId, editMessage.userId());
            Assertions.assertEquals(channelId, editMessage.channel());
            Assertions.assertEquals(messageId, editMessage.messageId());
            count++;
        }

        Assertions.assertEquals(1, count);
    }

    @Test
    public void whenDeletingMessageThenItAppearsInKafka() throws Exception {
        // given
        UUID userToken = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();


        // when
        ResultActions act = mockMvc.perform(delete("/chat/" + channelId+ "/" + messageId)
                .header("GW_TOKEN_ID", userToken.toString())
                .header("GW_USER_ID", userId.toString()));
        act.andExpect(status().isOk());

        // then
        ConsumerRecords<String, Object> records = kafkaConsumer.poll(Duration.ofMinutes(1));

        Assertions.assertFalse(records.isEmpty());

        int count = 0;

        for (ConsumerRecord<String, Object> record : records) {
            DeleteMessage deleteMessage = (DeleteMessage) record.value();

            Assertions.assertEquals(userToken, deleteMessage.tokenId());
            Assertions.assertEquals(userId, deleteMessage.userId());
            Assertions.assertEquals(channelId, deleteMessage.channel());
            Assertions.assertEquals(messageId, deleteMessage.messageId());
            count++;
        }

        Assertions.assertEquals(1, count);
    }


    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrapServers", kafka::getBootstrapServers);
    }

    static class KafkaConfiguration {
        @Bean
        public ProducerFactory<Object, Object> producerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.faust.chat.command.CommandSerializer.class);
            // more standard configuration
            return new DefaultKafkaProducerFactory<>(configProps);
        }
    }
}