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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@RunWith(SpringRunner.class)
@ExtendWith(E2ETestExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Main.class
)
@AutoConfigureWebTestClient
class SSEControllerTest extends E2ETestBase {

    // TODO: test timeouts, but in this case, it is needed to read content of flux

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void whenGettingSSEThenReturnEmpty() {
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
                .expectNoEvent(Duration.ofSeconds(3))
                .thenCancel()
                .verify();

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