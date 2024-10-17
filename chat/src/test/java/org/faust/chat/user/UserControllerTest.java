package org.faust.chat.user;

import org.faust.base.E2ETestBase;
import org.faust.base.E2ETestExtension;
import org.faust.chat.Main;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//FIXME: tests fail when ran together
@RunWith(SpringRunner.class)
@ExtendWith(E2ETestExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Main.class
)
@AutoConfigureWebTestClient
class UserControllerTest extends E2ETestBase implements ApplicationContextAware {

    @Autowired
    private WebTestClient webTestClient;

    private ApplicationContext applicationContext;

    @BeforeEach
    public void setUpForTest() {
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofMillis(300000))
                .build();
        resetUserBean();
    }

    @AfterEach
    public void resetUserBean() {
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        BeanDefinition bd = genericApplicationContext
                .getBeanDefinition("userRepository");
        genericApplicationContext.removeBeanDefinition("userRepository");
        genericApplicationContext.registerBeanDefinition("userRepository", bd);
        bd = genericApplicationContext
                .getBeanDefinition("userService");
        genericApplicationContext.removeBeanDefinition("userService");
        genericApplicationContext.registerBeanDefinition("userService", bd);
    }

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
    public void whenSettingAfkWithHookThenReturnedAfk() throws InterruptedException {
        // when
        Thread hookThread = new Thread( () -> webTestClient.post()
                .uri("/users/hook")
                .header("Authorization", getAuthorizationToken())
                .exchange()); // hook is infinite flux

        hookThread.start();

        Thread.sleep(1000); // ugly way to be sure that hook set up
        webTestClient.post()
                .uri("/users/afk")
                .header("Authorization", getAuthorizationToken())
                .exchange();

        // then
        Map<String, Collection<Map<String, String>>> result = webTestClient.get()
                .uri("/users")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Map.class)
                .getResponseBody()
                .single()
                .block();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertNotNull(result.get("ONLINE"));
        Assertions.assertNotNull(result.get("AFK"));
        Assertions.assertNotNull(result.get("OFFLINE"));
        Assertions.assertEquals("testuser", result.get("AFK").iterator().next().get("name"));

        hookThread.interrupt();
        hookThread.join();
    }

    @Test
    public void whenSettingAfkWithoutHookThenReturnedOffline() {
        // when
        webTestClient.post()
                .uri("/users/afk")
                .header("Authorization", getAuthorizationToken())
                .exchange();

        // then
        Map<String, Collection<Map<String, String>>> result = webTestClient.get()
                .uri("/users")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Map.class)
                .getResponseBody()
                .single()
                .block();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertNotNull(result.get("ONLINE"));
        Assertions.assertNotNull(result.get("AFK"));
        Assertions.assertNotNull(result.get("OFFLINE"));
        Assertions.assertEquals("testuser", result.get("OFFLINE").iterator().next().get("name"));
    }

    @Test
    public void whenSettingOnlineWithHookThenReturnedOnline() throws InterruptedException {
        // when
        Thread hookThread = new Thread( () -> webTestClient.post()
                .uri("/users/hook")
                .header("Authorization", getAuthorizationToken())
                .exchange()); // hook is infinite flux

        hookThread.start();

        Thread.sleep(1000); // ugly way to be sure that hook set up
        webTestClient.post()
                .uri("/users/online")
                .header("Authorization", getAuthorizationToken())
                .exchange();

        // then
        Map<String, Collection<Map<String, String>>> result = webTestClient.get()
                .uri("/users")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Map.class)
                .getResponseBody()
                .single()
                .block();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertNotNull(result.get("ONLINE"));
        Assertions.assertNotNull(result.get("AFK"));
        Assertions.assertNotNull(result.get("OFFLINE"));
        Assertions.assertEquals("testuser", result.get("ONLINE").iterator().next().get("name"));

        hookThread.interrupt();
        hookThread.join();
    }

    @Test
    public void whenSettingOnlineWithoutHookThenReturnedOffline() {
        // when
        webTestClient.post()
                .uri("/users/offline")
                .header("Authorization", getAuthorizationToken())
                .exchange();

        // then
        Map<String, Collection<Map<String, String>>> result = webTestClient.get()
                .uri("/users")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Map.class)
                .getResponseBody()
                .single()
                .block();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertNotNull(result.get("ONLINE"));
        Assertions.assertNotNull(result.get("AFK"));
        Assertions.assertNotNull(result.get("OFFLINE"));
        Assertions.assertEquals("testuser", result.get("OFFLINE").iterator().next().get("name"));
    }

    @Test
    public void whenSettingAfkAndOnlineWithHookThenReturnedOnline() throws InterruptedException {
        // when
        Thread hookThread = new Thread( () -> webTestClient.post()
                .uri("/users/hook")
                .header("Authorization", getAuthorizationToken())
                .exchange()); // hook is infinite flux

        hookThread.start();

        Thread.sleep(1000); // ugly way to be sure that hook set up
        webTestClient.post()
                .uri("/users/afk")
                .header("Authorization", getAuthorizationToken())
                .exchange();
        webTestClient.post()
                .uri("/users/online")
                .header("Authorization", getAuthorizationToken())
                .exchange();


        // then
        Map<String, Collection<Map<String, String>>> result = webTestClient.get()
                .uri("/users")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Map.class)
                .getResponseBody()
                .single()
                .block();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertNotNull(result.get("ONLINE"));
        Assertions.assertNotNull(result.get("AFK"));
        Assertions.assertNotNull(result.get("OFFLINE"));
        Assertions.assertEquals("testuser", result.get("ONLINE").iterator().next().get("name"));

        hookThread.interrupt();
        hookThread.join();
    }

    @Test
    public void whenSettingAfkAndOnlineWithoutHookThenReturnedOffline() {
        // when
        webTestClient.post()
                .uri("/users/afk")
                .header("Authorization", getAuthorizationToken())
                .exchange();
        webTestClient.post()
                .uri("/users/online")
                .header("Authorization", getAuthorizationToken())
                .exchange();


        // then
        Map<String, Collection<Map<String, String>>> result = webTestClient.get()
                .uri("/users")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Map.class)
                .getResponseBody()
                .single()
                .block();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertNotNull(result.get("ONLINE"));
        Assertions.assertNotNull(result.get("AFK"));
        Assertions.assertNotNull(result.get("OFFLINE"));
        Assertions.assertEquals("testuser", result.get("OFFLINE").iterator().next().get("name"));
    }

    @Test
    public void whenSettingOfflineWithHookThenReturnedOffline() throws InterruptedException {
        // when
        Thread hookThread = new Thread( () -> webTestClient.post()
                .uri("/users/hook")
                .header("Authorization", getAuthorizationToken())
                .exchange()); // hook is infinite flux

        hookThread.start();

        Thread.sleep(1000); // ugly way to be sure that hook set up
        webTestClient.post()
                .uri("/users/offline")
                .header("Authorization", getAuthorizationToken())
                .exchange();

        // then
        Map<String, Collection<Map<String, String>>> result = webTestClient.get()
                .uri("/users")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Map.class)
                .getResponseBody()
                .single()
                .block();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertNotNull(result.get("ONLINE"));
        Assertions.assertNotNull(result.get("AFK"));
        Assertions.assertNotNull(result.get("OFFLINE"));
        Assertions.assertEquals("testuser", result.get("OFFLINE").iterator().next().get("name"));

        hookThread.interrupt();
        hookThread.join();
    }

    @Test
    public void whenSettingOfflineWithoutHookThenReturnedOffline() {
        // when
        webTestClient.post()
                .uri("/users/offline")
                .header("Authorization", getAuthorizationToken())
                .exchange();

        // then
        Map<String, Collection<Map<String, String>>> result = webTestClient.get()
                .uri("/users")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Map.class)
                .getResponseBody()
                .single()
                .block();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertNotNull(result.get("ONLINE"));
        Assertions.assertNotNull(result.get("AFK"));
        Assertions.assertNotNull(result.get("OFFLINE"));
        Assertions.assertEquals("testuser", result.get("OFFLINE").iterator().next().get("name"));
    }

    @Test
    public void whenNoActivitySettingThenReturnedOffline() {
        // when
        // nothing

        // then
        Map<String, Collection<Map<String, String>>> result = webTestClient.get()
                .uri("/users")
                .header("Authorization", getAuthorizationToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Map.class)
                .getResponseBody()
                .single()
                .block();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertNotNull(result.get("ONLINE"));
        Assertions.assertNotNull(result.get("AFK"));
        Assertions.assertNotNull(result.get("OFFLINE"));
        Assertions.assertEquals("testuser", result.get("OFFLINE").iterator().next().get("name"));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}