package org.faust.chat.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "keycloak-repository")
public interface KeycloakService {
    @GetMapping("/keycloak/exists/{id}")
    boolean existsUser(@PathVariable("id") UUID uuid);
}
