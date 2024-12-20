package org.faust.user.command;

import java.util.UUID;

public record SetOnline(UUID userId, String username) {
}
