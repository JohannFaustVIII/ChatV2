package org.faust.channel;

import base.E2ETestBase;
import base.E2ETestExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.faust.channel.command.AddChannel;
import org.faust.channel.command.CommandSerializer;
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
import java.util.*;

import static java.lang.Thread.sleep;

// TODO: add postgres and liquibase files and setup

@RunWith(SpringRunner.class)
@ExtendWith(E2ETestExtension.class)
@Import(ChannelControllerTest.KafkaConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {ChannelRepositoryApplication.class, ChannelControllerTest.KafkaConfiguration.class}
)
@AutoConfigureMockMvc
@Testcontainers
class ChannelControllerTest extends E2ETestBase {

    @Container
    public static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("apache/kafka"));

    @Autowired
    private MockMvc mockMvc;

    KafkaTemplate<String, AddChannel> kafkaTemplate = new KafkaTemplate<>(KafkaConfiguration.greetingProducerFactory());

    @BeforeEach
    @AfterEach
    public void cleanDb() throws SQLException {
        try (Connection connection = databaseContainer.createConnection("")) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM \"channelTable\"");
        }
    }

    @Test
    public void givenChannelAddedWhenGettingChannelsThenReturned() throws Exception {
        // given
        Channel channel = new Channel(null, "Test Channel");
        UUID tokenid = UUID.randomUUID();

        kafkaTemplate.send("ADD_CHANNEL", new AddChannel(tokenid, channel));

        sleep(3000);
        // when
        ResultActions act = mockMvc.perform(MockMvcRequestBuilders.get("/channels"));
        Collection<Channel> result = readContent(act.andReturn().getResponse().getContentAsByteArray());

        // then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Test Channel", result.iterator().next().name());
    }

    @Test
    public void givenChannelAddedWhenCheckingExistenceThenTrue() throws Exception {
        // given
        Channel channel = new Channel(null, "Test Channel 2");
        UUID tokenid = UUID.randomUUID();

        kafkaTemplate.send("ADD_CHANNEL", new AddChannel(tokenid, channel));

        sleep(3000);

        ResultActions prev = mockMvc.perform(MockMvcRequestBuilders.get("/channels"));
        Collection<Channel> prevResult = readContent(prev.andReturn().getResponse().getContentAsByteArray());

        Channel addedChannel = prevResult.iterator().next();
        UUID id = addedChannel.id();

        // when
        ResultActions act = mockMvc.perform(MockMvcRequestBuilders.get("/channels/exists/" + id.toString()));
        boolean result = Boolean.parseBoolean(act.andReturn().getResponse().getContentAsString());

        // then
        Assertions.assertTrue(result);
    }

    private static Collection<Channel> readContent(byte[] data) throws IOException {
        CollectionType type = new ObjectMapper().getTypeFactory().constructCollectionType(List.class, Channel.class);
        return new ObjectMapper().readValue(data, type);
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
            configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.faust.channel.command.CommandDeserializer.class);
            configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-test");
            configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            // more standard configuration
            return new DefaultKafkaConsumerFactory<>(configProps);
        }

        public static ProducerFactory<String, AddChannel> greetingProducerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CommandSerializer.class);
            return new DefaultKafkaProducerFactory<>(configProps);
        }
    }
}