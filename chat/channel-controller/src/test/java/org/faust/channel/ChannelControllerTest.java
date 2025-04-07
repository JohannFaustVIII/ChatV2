package org.faust.channel;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.faust.channel.command.AddChannel;
import org.faust.channel.command.CommandDeserializer;
import org.junit.jupiter.api.Assertions;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import(ChannelControllerTest.KafkaConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {ChannelControllerApplication.class, ChannelControllerTest.KafkaConfiguration.class}
)
@AutoConfigureMockMvc
@Testcontainers
class ChannelControllerTest {

    @Container
    public static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("apache/kafka"));

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenAddingChannelThenItAppearsInKafka() throws Exception {
        // given
        UUID userToken = UUID.randomUUID();

        // when
        ResultActions act = mockMvc.perform(post("/channels")
                .header("GW_TOKEN_ID", userToken.toString())
                .contentType(MediaType.APPLICATION_JSON)
                        .content("Test Channel"));
        act.andExpect(status().isOk());

        // then
        KafkaConsumer<String, Object> consumer = new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "collector-test",
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"),
                new StringDeserializer(),
                new CommandDeserializer());

        consumer.subscribe(Collections.singletonList("ADD_CHANNEL"));

        ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMinutes(1));

        Assertions.assertFalse(records.isEmpty());

        int count = 0;

        for (ConsumerRecord<String, Object> record : records) {
            AddChannel channelCommand = (AddChannel) record.value();

            Assertions.assertEquals("Test Channel" , channelCommand.channel().name());
            Assertions.assertEquals(userToken, channelCommand.tokenId());
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
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.faust.channel.command.CommandSerializer.class);
            // more standard configuration
            return new DefaultKafkaProducerFactory<>(configProps);
        }
    }
}