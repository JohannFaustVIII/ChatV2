package org.faust.user.command;

import java.io.Serializable;
import java.util.UUID;

public record SetOffline(UUID userId, String username) implements Serializable {
}
