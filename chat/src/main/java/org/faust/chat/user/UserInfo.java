package org.faust.chat.user;

import java.util.UUID;

public record UserInfo(UUID userId, UserStatus status) {
}
