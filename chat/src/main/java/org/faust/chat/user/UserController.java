package org.faust.chat.user;

import org.faust.chat.config.AuthUser;
import org.faust.chat.keycloak.KeycloakService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Map;
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
    public Flux<Void> setActiveHook(@AuthenticationPrincipal AuthUser user) {
        return service.setActivityHook(user.getId());
    }

    @GetMapping
    public Map<UserStatus, Collection<UserDetails>> getUsers() {
        return this.service.getUsers();
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
