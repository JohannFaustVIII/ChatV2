package org.faust.user.command;

import java.util.UUID;

public record SetAfk(UUID userId, String username) {
}
