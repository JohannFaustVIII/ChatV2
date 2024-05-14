package org.faust.chat.user;

import org.faust.chat.config.AuthUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

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

}
