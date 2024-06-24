package org.faust.chat.user;

import org.faust.chat.config.AuthUser;
import org.faust.chat.keycloak.KeycloakService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService service;

    private final KeycloakService keycloakService;
    public UserController(UserService service, KeycloakService keycloakService) {
        this.service = service;
        this.keycloakService = keycloakService;
    }

    @PostMapping("/active")
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

    @GetMapping
    public Collection<UserInfo> getActiveUsers() {
        return this.service.getActiveUsers();
    }

    @GetMapping("/details")
    public Collection<UserDetails> getUserDetails() {
        return keycloakService.getUsers();
    }
}
