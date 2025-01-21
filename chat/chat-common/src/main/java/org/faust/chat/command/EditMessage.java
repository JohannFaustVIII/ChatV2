package org.faust.chat.command;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record EditMessage(UUID tokenId, UUID channel, UUID messageId, UUID userId, String newMessage, LocalDateTime editTime) implements Serializable {
}
