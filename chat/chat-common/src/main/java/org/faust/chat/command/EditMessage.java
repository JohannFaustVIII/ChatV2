package org.faust.chat.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record EditMessage(UUID channel, UUID messageId, UUID userId, String newMessage, LocalDateTime editTime) {
}
