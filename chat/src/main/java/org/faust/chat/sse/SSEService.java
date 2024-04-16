package org.faust.chat.sse;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class SSEService {

    private final Sinks.Many processor;


    public SSEService() {
        this.processor =  Sinks.many().multicast().directBestEffort();
    }

    public void emitEvents(String event) {
        this.processor.tryEmitNext(event);
    }

    public Flux<String> getEvents() {
        return processor.asFlux();
    }
}
