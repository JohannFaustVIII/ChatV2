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
        // when-then
        Flux<String> sse = testedService.getEvents();
        StepVerifier
                .create(sse)
                .then(() -> testedService.emitEvents(eventString))
                .expectNext(eventString)
                .thenCancel()
                .verify();
    }

    @Test
    public void whenEmittingMultipleEventsThenAllReceived() {
        // given
        SSEService testedService = new SSEService();
        String eventString = "Random text supposed to be Event";
        String eventString2 = "Update Event";
        String eventString3 = "Third Emergency Event";
        // when-then
        Flux<String> sse = testedService.getEvents();
        StepVerifier
                .create(sse)
                .then(() -> {
                    testedService.emitEvents(eventString);
                    testedService.emitEvents(eventString2);
                    testedService.emitEvents(eventString3);
                })
                .expectNext(eventString)
                .expectNext(eventString2)
                .expectNext(eventString3)
                .thenCancel()
                .verify();
    }

    @Test
    public void whenEmittingMultipleSeparateEventsThenAllReceived() {
        // given
        SSEService testedService = new SSEService();
        String eventString = "Random text supposed to be Event";
        String eventString2 = "Update Event";
        String eventString3 = "Third Emergency Event";
        // when-then
        Flux<String> sse = testedService.getEvents();
        StepVerifier
                .create(sse)
                .then(() -> testedService.emitEvents(eventString))
                .expectNext(eventString)
                .then(() -> testedService.emitEvents(eventString2))
                .expectNext(eventString2)
                .then(() -> testedService.emitEvents(eventString3))
                .expectNext(eventString3)
                .thenCancel()
                .verify();
    }


}