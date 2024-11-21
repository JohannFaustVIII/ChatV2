package org.faust.chat.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

//@FeignClient(name = "keycloak-repository") //TODO: to interface?
@Component
public class KeycloakService {
    public boolean existsUser(UUID uuid) {
        return false;
    }
}
