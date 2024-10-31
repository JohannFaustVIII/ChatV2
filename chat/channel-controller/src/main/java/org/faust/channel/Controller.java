package org.faust.channel;

import org.faust.config.AuthUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/channels")
public class Controller {

    @Value("${spring.kafka.bootstrapServers}")
    private String testProperty;

    @GetMapping
    @RequestMapping("/get")
    public String getChannel(@AuthenticationPrincipal AuthUser user, @RequestHeader Map<String, String> headers) {
        return testProperty + " " + user.getName(); //TODO: so, it doesn't work, but headers are passed to it, looks like requires @EnableWebFluxSecurity and others "turn on"s, think how to solve that gracefully
    }
}
