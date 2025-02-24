package org.faust.chat.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Component
@FeignClient(name = "keycloak-repository")
public interface KeycloakService {
    @GetMapping("/keycloak/exists/{id}")
    boolean existsUser(@PathVariable("id") UUID uuid);
}
