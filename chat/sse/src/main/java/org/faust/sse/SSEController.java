package org.faust.sse;

import org.faust.config.AuthUser;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/events")
public class SSEController {

    private final SSEService service;

    public SSEController(SSEService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> eventStream(@AuthenticationPrincipal AuthUser user) { // TODO: as it is flux, it requires another way, think about gateway again
        return service.getEvents(user.getId());
    }
}
