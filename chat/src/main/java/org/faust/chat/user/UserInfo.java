package org.faust.chat.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserInfo(UUID userId, String username, UserStatus status, LocalDateTime updateTime) {
}
