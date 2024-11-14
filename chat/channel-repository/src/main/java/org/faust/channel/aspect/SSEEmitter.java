package org.faust.channel.aspect;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SSEEmitter {

    private static final String SSE_EVENTS = "SSE_EVENTS";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public SSEEmitter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void emitEvent(String event) {
        kafkaTemplate.send(SSE_EVENTS, event, event);
    }
}
