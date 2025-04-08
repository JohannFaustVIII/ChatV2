package org.faust.channel;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@Import(ChannelControllerTest.KafkaConfiguration.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {ChannelRepositoryApplication.class, ChannelControllerTest.KafkaConfiguration.class}
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
    public void givenChannelAddedWhenGettingChannelsThenReturned() {
        // given
        // use kafka producer
        // when

        // then
    }

    @Test
    public void givenChannelAddedWhenCheckingExistenceThenTrue() {

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
            // more standard configuration
            return new DefaultKafkaConsumerFactory<>(configProps);
        }
    }
}