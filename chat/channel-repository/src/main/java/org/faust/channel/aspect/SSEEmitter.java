package org.faust.channel.aspect;

import org.faust.sse.Message;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SSEEmitter {

    private static final String SSE_EVENTS = "SSE_EVENTS";

    private final KafkaTemplate<String, Message> kafkaTemplate;

    public SSEEmitter(KafkaTemplate<String, Message> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void emitEvent(String event) {
        kafkaTemplate.send(SSE_EVENTS, event, Message.globalNotify(event));
    }
}
