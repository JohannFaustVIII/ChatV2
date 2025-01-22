package org.faust.chat.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.faust.chat.command.AddMessage;
import org.faust.chat.command.CommandDeserializer;
import org.faust.chat.command.CommandSerde;
import org.faust.chat.command.CommandSerializer;
import org.faust.chat.external.ChannelService;
import org.faust.chat.external.ChatService;
import org.faust.chat.external.KeycloakService;
import org.faust.sse.Message;
import org.faust.sse.Target;
import org.faust.sse.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageFilterStreamTest {

    @Mock
    ChannelService channelService;

    @Mock
    ChatService chatService;

    @Mock
    KeycloakService keycloakService;

    @Mock
    KafkaTemplate<String, Message> kafkaTemplate;

    @InjectMocks
    MessageFilterStream testedStream;

    @Captor
    ArgumentCaptor<Message> argumentCaptor;

    @Test
    public void whenAddCorrectMessageThenSendForward() {
        // given
        TopologyTestDriver testDriver = prepareTopology();

        TestInputTopic<String, Object> inputTopic = testDriver.createInputTopic("CHAT_REQUEST", new StringSerializer(), new CommandSerializer());
        TestOutputTopic<String, Object> outputTopic = testDriver.createOutputTopic("CHAT_COMMAND", new StringDeserializer(), new CommandDeserializer());

        UUID tokenId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        String senderName = "RandomSender";
        UUID senderId = UUID.randomUUID();
        String message = "Random message";
        LocalDateTime time = LocalDateTime.now();

        Mockito.when(keycloakService.existsUser(senderId)).thenReturn(true);
        Mockito.when(channelService.existsChannel(channelId)).thenReturn(true);

        // when
        inputTopic.pipeInput(new AddMessage(tokenId, channelId, senderName, senderId,  message, time));

        // then
        assertInstanceOf(AddMessage.class, outputTopic.readRecord().getValue());
    }

    @Test
    public void whenAddMessageByWrongUserThenException() {
        // given
        TopologyTestDriver testDriver = prepareTopology();

        TestInputTopic<String, Object> inputTopic = testDriver.createInputTopic("CHAT_REQUEST", new StringSerializer(), new CommandSerializer());
        TestOutputTopic<String, Object> outputTopic = testDriver.createOutputTopic("CHAT_COMMAND", new StringDeserializer(), new CommandDeserializer());

        UUID tokenId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        String senderName = "RandomSender";
        UUID senderId = UUID.randomUUID();
        String message = "Random message";
        LocalDateTime time = LocalDateTime.now();

        Mockito.when(keycloakService.existsUser(senderId)).thenReturn(false);

        // when
        inputTopic.pipeInput(new AddMessage(tokenId, channelId, senderName, senderId,  message, time));

        // then

        assertEquals(0, outputTopic.getQueueSize());
        verify(kafkaTemplate).send(eq("SSE_EVENTS"), eq(tokenId.toString()), argumentCaptor.capture());
        Message errorMessage = argumentCaptor.getValue();
        assertErrorMessage("Requested user not found.", tokenId, errorMessage);
    }

    @Test
    public void whenAddMessageToWrongChannelThenException() {
        // given
        TopologyTestDriver testDriver = prepareTopology();

        TestInputTopic<String, Object> inputTopic = testDriver.createInputTopic("CHAT_REQUEST", new StringSerializer(), new CommandSerializer());
        TestOutputTopic<String, Object> outputTopic = testDriver.createOutputTopic("CHAT_COMMAND", new StringDeserializer(), new CommandDeserializer());

        UUID tokenId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        String senderName = "RandomSender";
        UUID senderId = UUID.randomUUID();
        String message = "Random message";
        LocalDateTime time = LocalDateTime.now();

        Mockito.when(keycloakService.existsUser(senderId)).thenReturn(true);
        Mockito.when(channelService.existsChannel(channelId)).thenReturn(false);

        // when
        inputTopic.pipeInput(new AddMessage(tokenId, channelId, senderName, senderId,  message, time));

        // then

        assertEquals(0, outputTopic.getQueueSize());
        verify(kafkaTemplate).send(eq("SSE_EVENTS"), eq(tokenId.toString()), argumentCaptor.capture());
        Message errorMessage = argumentCaptor.getValue();
        assertErrorMessage("Requested channel not found.", tokenId, errorMessage);
    }

    private static void assertErrorMessage(String expectedMessage, UUID tokenId, Message resultMessage) {
        assertEquals(expectedMessage, resultMessage.message());
        assertEquals(tokenId, resultMessage.tokenId());
        assertEquals(Target.USER, resultMessage.target());
        assertEquals(Type.NOTIFICATION, resultMessage.type());
    }

    private TopologyTestDriver prepareTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        testedStream.requestFilter(streamsBuilder);
        Properties props = new Properties();
        props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CommandSerde.class.getName());

        TopologyTestDriver testDriver = new TopologyTestDriver(streamsBuilder.build(), props);
        return testDriver;
    }


}