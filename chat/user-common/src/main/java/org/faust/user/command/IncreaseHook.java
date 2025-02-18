package org.faust.user.command;

import java.io.Serializable;
import java.util.UUID;

public record IncreaseHook(UUID userId) implements Serializable {
}
