package org.faust.chat;

import java.util.UUID;

// TODO: duplicated with keycloak-repository
public record UserDetails(UUID id, String name) {
}
