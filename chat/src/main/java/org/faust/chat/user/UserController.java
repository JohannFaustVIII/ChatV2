package org.faust.chat.user;

import org.faust.chat.config.AuthUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    // TODO: NOPE As long as frontend is open, status is afk, so stream that info maybe to avoid all time requests to backend? Flux as parameter
    // TODO: What if front end was sending event of inactivity after some time, and about activity if it is returned, and offline if event was closed?
    // ^ What if multiple event streams opened? To be resolved by server?
    // TODO: active means online, so there is any activity on frontend, like mouse movement etc., or requests are sent? here AOP (before) could work to monitor that

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
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
    public List<UserInfo> getActiveUsers() {
        return this.service.getActiveUsers();
    }

}
