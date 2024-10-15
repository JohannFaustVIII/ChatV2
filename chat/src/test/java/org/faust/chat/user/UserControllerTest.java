package org.faust.chat.user;

import org.faust.base.E2ETestBase;
import org.faust.base.E2ETestExtension;
import org.faust.chat.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@ExtendWith(E2ETestExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Main.class
)
@AutoConfigureWebTestClient
class UserControllerTest extends E2ETestBase {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void whenGettingUsersDetailsThenAllReturned() {
        // when
        List<UserDetails> result = webTestClient.get()
                .uri("/users/details")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(UserDetails.class)
                .getResponseBody()
                .collectList()
                .block();

        // then
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(E2ETestBase.KEYCLOAK_USER.toLowerCase(), result.get(0).name());
        Assertions.assertEquals(E2ETestBase.KEYCLOAK_USER_ID, result.get(0).id());
    }

    @Test
    public void whenGettingExistingUserDetailsThenReturned() {
        // when
        UserDetails result = webTestClient.get()
                .uri("/users/details/" + E2ETestBase.KEYCLOAK_USER_ID)
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(UserDetails.class)
                .getResponseBody()
                .single()
                .block();

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(E2ETestBase.KEYCLOAK_USER.toLowerCase(), result.name());
        Assertions.assertEquals(E2ETestBase.KEYCLOAK_USER_ID, result.id());
    }

    @Test
    public void whenGettingNotExistingUserDetailsThenException() {
        // given
        UUID notExistingUserId = null;
        while (notExistingUserId == null || notExistingUserId == E2ETestBase.KEYCLOAK_USER_ID) {
            notExistingUserId = UUID.randomUUID();
        }
        // when
        Map<String, Object> result = webTestClient.get()
                .uri("/users/details/" + notExistingUserId)
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isNotFound()
                .returnResult(Map.class)
                .getResponseBody()
                .single()
                .block();

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Requested user not found.", result.get("message"));
    }

    // offline/afk/online depends on activity hook, and are implemented to keep the state, so may affect other tests, TODO: think

    @Test
    public void whenSettingAfkWithHookThenReturnedAfk() {

    }

    @Test
    public void whenSettingAfkWithoutHookThenReturnedOffline() {

    }

    @Test
    public void whenSettingOnlineWithHookThenReturnedOnline() {

    }

    @Test
    public void whenSettingOnlineWithoutHookThenReturnedOffline() {

    }

    @Test
    public void whenSettingAfkAndOnlineWithHookThenReturnedOnline() {

    }

    @Test
    public void whenSettingAfkAndOnlineWithoutHookThenReturnedOffline() {

    }

    @Test
    public void whenSettingOfflineWithHookThenReturnedOffline() {

    }

    @Test
    public void whenSettingOfflineWithoutHookThenReturnedOffline() {

    }

    @Test
    public void whenNoActivitySettingThenReturnedOffline() {

    }
}