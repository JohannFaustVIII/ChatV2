package org.faust.chat.channel;

import org.faust.base.E2ETestBase;
import org.faust.chat.Main;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Main.class
)
@AutoConfigureWebTestClient
class ChannelControllerTest extends E2ETestBase {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void whenGetNoChannelsThenEmptyReturned() {
        webTestClient.get()
                .uri("/channels")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");
    }

    // TODO: more tests and remove existing channels before each test? - would require direct access to db for now
}