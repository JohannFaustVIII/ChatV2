package org.faust.sse;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Service
public class SSEService {

    private final Sinks.Many<Message> processor;


    public SSEService() {
        this.processor =  Sinks.many().multicast().directBestEffort();
    }

    public void emitEvents(Message event) {
        this.processor.tryEmitNext(event);
    }

    public Flux<String> getEvents(UUID user, UUID tokenId) {
        return processor.asFlux()
                .filter(event -> Target.ALL.equals(event.target()) || tokenId == null || tokenId.equals(event.tokenId()))
                .map(event -> event.message());
    }
}
