package org.faust.sse;

import java.util.UUID;

public record Message(Type type, Target target, UUID targetInfo, String message) {

    public static Message globalNotify(String message) {
        return new Message(Type.NOTIFICATION, Target.ALL, null, message);
    }

    public static Message userNotify(UUID user, String message) {
        return new Message(Type.NOTIFICATION, Target.USER, user, message);
    }

    public static Message error(UUID user, String message) {
        return new Message(Type.NOTIFICATION, Target.USER, user, message);
    }
}
