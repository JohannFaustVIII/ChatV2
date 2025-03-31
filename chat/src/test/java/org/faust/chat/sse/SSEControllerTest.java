package org.faust.chat.sse;

import org.faust.base.E2ETestBase;
import org.faust.base.E2ETestExtension;
import org.faust.chat.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@ExtendWith(E2ETestExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Main.class
)
@AutoConfigureWebTestClient
class SSEControllerTest extends E2ETestBase {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void whenGettingSSEThenReturnEmpty() throws InterruptedException {
        Thread sseThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            webTestClient.post()
                    .uri("/channels")
                    .header("Authorization", getAuthorizationToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("Channel 1")
                    .exchange()
                    .expectStatus().isOk();
            webTestClient.post()
                    .uri("/channels")
                    .header("Authorization", getAuthorizationToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("Channel 2")
                    .exchange()
                    .expectStatus().isOk();
        });
        sseThread.start();

        Flux<String> sse = webTestClient
                .get()
                .uri("/events")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier
                .create(sse)
                .expectNext("channel")
                .expectNext("channel")
//                .expectNext("user")
                .thenCancel()
                .verify();

        sseThread.join();
    }

    @Test
    public void whenChannelAddedThenEventSentViaSSE() {

    }

    @Test
    public void whenMessageSentThenEventSentViaSSE() {

    }

    @Test
    public void whenUserActivityChangeThenEventSentViaSSE() {

    }

}