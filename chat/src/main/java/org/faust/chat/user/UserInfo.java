package org.faust.chat.user;

import java.util.UUID;

// TODO: it doesn't contain username anymore, requires to do another way to get usernames
public record UserInfo(UUID userId, UserStatus status) {
}
