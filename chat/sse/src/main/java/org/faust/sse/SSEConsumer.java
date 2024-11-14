package org.faust.sse;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SSEConsumer {
    private static final String SSE_EVENTS = "SSE_EVENTS";

    private final SSEService sseService;

    public SSEConsumer(SSEService sseService) {
        this.sseService = sseService;
    }

    @KafkaListener(topics = SSE_EVENTS)
    public void getEvent(String event) {
        sseService.emitEvents(event);
    }
}
