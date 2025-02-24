package org.faust.sse;

import java.util.UUID;

public record Message(Type type, Target target, UUID tokenId, String message) {

    public static Message globalNotify(String message) {
        return new Message(Type.NOTIFICATION, Target.ALL, null, message);
    }

    public static Message userNotify(UUID tokenId, String message) {
        return new Message(Type.NOTIFICATION, Target.USER, tokenId, message);
    }

    public static Message error(UUID tokenId, String message) {
        return new Message(Type.NOTIFICATION, Target.USER, tokenId, message);
    }
}
