package org.faust.chat.command;

import java.io.Serializable;
import java.util.UUID;

public record DeleteMessage(UUID tokenId, UUID channel, UUID messageId, UUID userId) implements Serializable {
}
