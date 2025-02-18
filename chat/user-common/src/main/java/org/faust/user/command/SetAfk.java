package org.faust.user.command;

import java.io.Serializable;
import java.util.UUID;

public record SetAfk(UUID userId, String username) implements Serializable {
}
