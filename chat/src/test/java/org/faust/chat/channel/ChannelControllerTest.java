package org.faust.chat.channel;

import org.faust.base.E2ETestBase;
import org.faust.chat.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Main.class
)
@AutoConfigureWebTestClient
class ChannelControllerTest extends E2ETestBase {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void cleanDb() throws SQLException {
        try (Connection connection = databaseContainer.createConnection("")) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM \"channelTable\"");
        }
    }

    @Test
    public void whenGetNoChannelsThenEmptyReturned() {
        // when-then
        webTestClient.get()
                .uri("/channels")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");
    }

    @Test
    public void whenGetExistingChannelsThenAllReturned() {
        // given
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
        // when-then
        webTestClient.get()
                .uri("/channels")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(
                        result -> {
                            Assertions.assertTrue(result.getResponseBody().contains("Channel 1"));
                            Assertions.assertTrue(result.getResponseBody().contains("Channel 2"));
                        }
                );
    }

    // TODO: more tests and remove existing channels before each test? - would require direct access to db for now
}