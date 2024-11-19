package org.faust.chat.command;

import java.util.UUID;

public record EditMessage(UUID channel, UUID messageId, UUID userId, String newMessage) {
}
