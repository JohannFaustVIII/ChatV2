package org.faust.chat.user;

import org.faust.chat.config.AuthUser;
import org.faust.chat.keycloak.KeycloakService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService service;

    private final KeycloakService keycloakService;
    public UserController(UserService service, KeycloakService keycloakService) {
        this.service = service;
        this.keycloakService = keycloakService;
    }

    @PostMapping("/online")
    public void setActive(@AuthenticationPrincipal AuthUser user) {
        service.setActive(user.getId(), user.getName());
    }

    @PostMapping("/afk")
    public void setAfk(@AuthenticationPrincipal AuthUser user) {
        service.setAfk(user.getId(), user.getName());
    }

    @PostMapping("/offline")
    public void setOffline(@AuthenticationPrincipal AuthUser user) {
        service.setOffline(user.getId(), user.getName());
    }

    @PostMapping("/hook")
    public Flux<Void> setActiveHook() {
        return service.setActivityHook();
    }

    @GetMapping
    public Collection<UserInfo> getActiveUsers() {
        return this.service.getActiveUsers();
    }

    @GetMapping("/details")
    public Collection<UserDetails> getUserDetails() {
        return keycloakService.getUsers();
    }

    @GetMapping("/details/{id}")
    public UserDetails getUser(@PathVariable UUID id) {
        return keycloakService.getUserInfo(id);
    }
}
