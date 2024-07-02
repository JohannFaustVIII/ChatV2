package org.faust.chat.sse;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class SSEService {

    private final Sinks.Many<String> processor;


    public SSEService() {
        this.processor =  Sinks.many().multicast().directBestEffort();
    }

    public void emitEvents(String event) {
        this.processor.tryEmitNext(event);
    }

    public Flux<String> getEvents() {
        return processor.asFlux()
                .doOnCancel(
                () -> System.out.println("Flux was cancelled")); // if cancel happened, then front-end probably was closed, but:
        // what if multiple frontends are open? how to distinguish them?
        // how to get info about the user? this endpoint doesn't require authorization header
    }
}
