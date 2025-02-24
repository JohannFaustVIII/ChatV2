package org.faust.chat.external;

import org.faust.chat.UserDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Service
@FeignClient(name = "keycloak-repository")
public interface KeycloakRepository {

    @GetMapping("/keycloak/details")
    Collection<UserDetails> getUsers();
}
