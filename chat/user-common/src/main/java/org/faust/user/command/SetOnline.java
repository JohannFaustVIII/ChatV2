package org.faust.user.command;

import java.io.Serializable;
import java.util.UUID;

public record SetOnline(UUID userId, String username) implements Serializable {
}
