package org.faust.sse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/events")
public class SSEController {

    private final SSEService service;

    public SSEController(SSEService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> eventStream(@RequestHeader(value = "GW_USER_ID") UUID userId, @RequestHeader(value = "GW_TOKEN_ID") UUID tokenId) {
        return service.getEvents(userId, tokenId);
    }
}
