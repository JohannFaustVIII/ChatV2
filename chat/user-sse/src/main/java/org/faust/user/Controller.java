package org.faust.user;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class Controller {
    private final UserService service;

//    private final KeycloakService keycloakService;
    public Controller(UserService service) {
        this.service = service;
    }

    @PostMapping("/online")
    public void setActive(@RequestHeader("GW_USER") String username, @RequestHeader("GW_USER_ID") UUID userId) {
        service.setActive(userId, username);
    }

    @PostMapping("/afk")
    public void setAfk(@RequestHeader("GW_USER") String username, @RequestHeader("GW_USER_ID") UUID userId) {
        service.setAfk(userId, username);
    }

    @PostMapping("/offline")
    public void setOffline(@RequestHeader("GW_USER") String username, @RequestHeader("GW_USER_ID") UUID userId) {
        service.setOffline(userId, username);
    }

    @PostMapping("/hook")
    public Flux<Void> setActiveHook(@RequestHeader("GW_USER") String username, @RequestHeader("GW_USER_ID") UUID userId) {
        return service.setActivityHook(userId);
    }

    // TODO: below to user-repository

//    @GetMapping
//    public Map<UserStatus, Collection<UserDetails>> getUsers() {
//        return this.service.getUsers();
//    }
//
//    @GetMapping("/details")
//    public Collection<UserDetails> getUserDetails() {
//        return keycloakService.getUsers();
//    }
//
//    @GetMapping("/details/{id}")
//    public UserDetails getUser(@PathVariable UUID id) {
//        return keycloakService.getUserInfo(id);
//    }
}
