package org.faust.chat.sse;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class SSEServiceTest {

    @Test
    public void whenEmittingEventThenReceived() {
        // given
        SSEService testedService = new SSEService();
        String eventString = "Random text supposed to be Event";
        // when
        Flux<String> sse = testedService.getEvents();
//        testedService.emitEvents(eventString);

        // then
        StepVerifier
                .create(sse)
                .then(() -> testedService.emitEvents(eventString))
                .expectNext(eventString)
                .thenCancel()
                .verify();
    }

}