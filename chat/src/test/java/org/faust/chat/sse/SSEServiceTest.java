package org.faust.chat.sse;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class SSEServiceTest {

    @Test
    public void whenEmittingEventThenReceived() {
        // given
        SSEService testedService = new SSEService();
        String eventString = "Random text supposed to be Event";
        // when
        Flux<String> sse = testedService.getEvents();
        testedService.emitEvents(eventString);
        // then
        StepVerifier
                .create(sse)
                .expectNext(eventString + "X"); //FIXME: that shows that it doesn't work
    }

}