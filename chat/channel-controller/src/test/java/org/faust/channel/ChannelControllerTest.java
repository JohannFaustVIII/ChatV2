//package org.faust.channel;
//
////import org.faust.base.E2ETestBase;
////import org.faust.base.E2ETestExtension;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
////import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.reactive.server.WebTestClient;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.List;
//
//TODO: REQUIRES BOTH SERVICES
//
////@RunWith(SpringRunner.class)
////@ExtendWith(E2ETestExtension.class)
//@SpringBootTest(
//        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
//        classes = ChannelControllerApplication.class
//)
//@AutoConfigureWebTestClient
////class ChannelControllerTest extends E2ETestBase {
//
////    @Autowired
////    private WebTestClient webTestClient;
////
////    @BeforeEach
////    @AfterEach
////    public void cleanDb() throws SQLException {
////        try (Connection connection = databaseContainer.createConnection("")) {
////            Statement stmt = connection.createStatement();
////            stmt.executeUpdate("DELETE FROM \"channelTable\"");
////        }
////    }
////
////    @Test
////    public void whenGetNoChannelsThenEmptyReturned() {
////        // when-then
////        webTestClient.get()
////                .uri("/channels")
////                .header("Authorization", getAuthorizationToken())
////                .exchange()
////                .expectStatus().isOk()
////                .expectBody().json("[]");
////    }
////
////    @Test
////    public void whenGetExistingChannelsThenAllReturned() {
////        // given
////        webTestClient.post()
////                .uri("/channels")
////                .header("Authorization", getAuthorizationToken())
////                .contentType(MediaType.APPLICATION_JSON)
////                .bodyValue("Channel 1")
////                .exchange()
////                .expectStatus().isOk();
////        webTestClient.post()
////                .uri("/channels")
////                .header("Authorization", getAuthorizationToken())
////                .contentType(MediaType.APPLICATION_JSON)
////                .bodyValue("Channel 2")
////                .exchange()
////                .expectStatus().isOk();
////        // when
////        List<Channel> result = webTestClient.get()
////                .uri("/channels")
////                .header("Authorization", getAuthorizationToken())
////                .exchange()
////                .expectStatus().isOk()
////                .returnResult(Channel.class)
////                .getResponseBody()
////                .collectList()
////                .block();
////
////        // then
////        Assertions.assertEquals(2, result.size());
////        Assertions.assertEquals("Channel 1", result.get(0).name());
////        Assertions.assertEquals("Channel 2", result.get(1).name());
////    }
////
////    @Test
////    public void whenChannelAddedThenReturned() {
////        // when
////        webTestClient.post()
////                .uri("/channels")
////                .header("Authorization", getAuthorizationToken())
////                .contentType(MediaType.APPLICATION_JSON)
////                .bodyValue("Test Channel")
////                .exchange()
////                .expectStatus().isOk();
////        // then
////        List<Channel> result = webTestClient.get()
////                .uri("/channels")
////                .header("Authorization", getAuthorizationToken())
////                .exchange()
////                .expectStatus().isOk()
////                .returnResult(Channel.class)
////                .getResponseBody()
////                .collectList()
////                .block();
////
////        Assertions.assertEquals(1, result.size());
////        Assertions.assertEquals("Test Channel", result.get(0).name());
////    }
//}