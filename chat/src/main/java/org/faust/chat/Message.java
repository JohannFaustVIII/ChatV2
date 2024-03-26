package org.faust.chat;

import java.time.LocalDateTime;
import java.util.UUID;

public record Message(UUID id, String message, LocalDateTime serverTime) {
}
