package org.faust.chat.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record AddMessage(UUID channel, String sender, UUID senderId, String message, LocalDateTime sendTime) {
}
