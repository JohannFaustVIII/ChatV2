package org.faust.chat.command;

import java.util.UUID;

public record DeleteMessage(UUID requesterId, UUID channel, UUID messageId, UUID userId) {
}
