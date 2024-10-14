package org.faust.chat.chat;

import org.faust.base.E2ETestBase;
import org.faust.base.E2ETestExtension;
import org.faust.chat.Main;
import org.faust.chat.channel.Channel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@ExtendWith(E2ETestExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Main.class
)
@AutoConfigureWebTestClient
class ChatControllerTest extends E2ETestBase {

    @Autowired
    private WebTestClient webTestClient;

    private UUID mainChannelUUID;
    private UUID secondChannelUUID;

    @BeforeEach
    public void setUpSingleEmptyChannel() throws SQLException {
        cleanDb();
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
                .bodyValue("Other Channel")
                .exchange()
                .expectStatus().isOk();
        mainChannelUUID = webTestClient.get()
                .uri("/channels")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Channel.class)
                .getResponseBody()
                .collectList()
                .block()
                .stream().filter(c -> c.name().equals("Channel 1"))
                .findFirst()
                .get()
                .id();
        secondChannelUUID = webTestClient.get()
                .uri("/channels")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Channel.class)
                .getResponseBody()
                .collectList()
                .block()
                .stream().filter(c -> c.name().equals("Other Channel"))
                .findFirst()
                .get()
                .id();
    }

    @AfterEach
    public static void cleanDb() throws SQLException {
        try (Connection connection = databaseContainer.createConnection("")) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM \"messageTable\"");
            stmt.executeUpdate("DELETE FROM \"channelTable\"");
        }
    }

    @Test
    public void whenGettingNoMessagesThenEmpty() {
        // when
        List<Message> result = webTestClient.get()
                .uri("/chat/" + mainChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Message.class)
                .getResponseBody()
                .collectList()
                .block();
        // then
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void whenGettingExistingMessagesThenAllReturned() {
        // given
        webTestClient.post()
                .uri("/chat/" + mainChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("Test Message")
                .exchange()
                .expectStatus().isOk();
        // when
        List<Message> result = webTestClient.get()
                .uri("/chat/" + mainChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Message.class)
                .getResponseBody()
                .collectList()
                .block();
        // then
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Test Message", result.get(0).message());
    }

    @Test
    public void whenGettingMessagesThenReturnedOnlyFromGivenChannel() {
        // given
        webTestClient.post()
                .uri("/chat/" + mainChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("Test Message")
                .exchange()
                .expectStatus().isOk();
        webTestClient.post()
                .uri("/chat/" + secondChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("Other Message")
                .exchange()
                .expectStatus().isOk();
        // when
        List<Message> result = webTestClient.get()
                .uri("/chat/" + mainChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Message.class)
                .getResponseBody()
                .collectList()
                .block();
        // then
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Test Message", result.get(0).message());
    }

    @Test
    public void whenMessageAddedThenReturned() {
        // when
        webTestClient.post()
                .uri("/chat/" + mainChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("Test Message")
                .exchange()
                .expectStatus().isOk();
        // then
        List<Message> result = webTestClient.get()
                .uri("/chat/" + mainChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Message.class)
                .getResponseBody()
                .collectList()
                .block();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Test Message", result.get(0).message());
    }

    @Test
    public void whenMessagedEditedThenChangeVisible() {
        // given
        webTestClient.post()
                .uri("/chat/" + mainChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("Test Message")
                .exchange()
                .expectStatus().isOk();
        UUID messageId = webTestClient.get()
                .uri("/chat/" + mainChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Message.class)
                .getResponseBody()
                .collectList()
                .block()
                .get(0)
                .id();
        // when
        webTestClient.put()
                .uri("/chat/" + mainChannelUUID + "/" + messageId)
                .header("Authorization", getAuthorizationToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("Edited Message")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Message.class)
                .getResponseBody()
                .collectList()
                .block();
        // then
        List<Message> result = webTestClient.get()
                .uri("/chat/" + mainChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Message.class)
                .getResponseBody()
                .collectList()
                .block();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Edited Message", result.get(0).message());
    }

    @Test
    public void whenMessageDeletedThenNotReturned() {
        // given
        webTestClient.post()
                .uri("/chat/" + mainChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("Test Message")
                .exchange()
                .expectStatus().isOk();
        UUID messageId = webTestClient.get()
                .uri("/chat/" + mainChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Message.class)
                .getResponseBody()
                .collectList()
                .block()
                .get(0)
                .id();
        // when
        webTestClient.delete()
                .uri("/chat/" + mainChannelUUID + "/" + messageId)
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Message.class)
                .getResponseBody()
                .collectList()
                .block();
        // then
        List<Message> result = webTestClient.get()
                .uri("/chat/" + mainChannelUUID)
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Message.class)
                .getResponseBody()
                .collectList()
                .block();
        Assertions.assertTrue(result.isEmpty());
    }

}