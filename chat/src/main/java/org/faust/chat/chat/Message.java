package org.faust.chat.chat;

import java.time.LocalDateTime;
import java.util.UUID;

public record Message(UUID id, UUID channelId, String sender, String message, LocalDateTime serverTime) {
}
