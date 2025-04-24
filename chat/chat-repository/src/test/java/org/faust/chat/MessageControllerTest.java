package org.faust.chat;

import base.E2ETestBase;
import base.E2ETestExtension;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.faust.chat.command.CommandSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// TODO: there is endpoint to get all messages, and via kafka to edit data; so put data via kafka and then check existence? clean db between calls
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