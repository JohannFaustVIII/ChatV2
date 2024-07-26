package org.faust.chat.channel;

import org.jooq.Record;

import java.util.UUID;

public record Channel(UUID id, String name) {

    public static Channel mapToChannel(Record record) {
        return new Channel(
                record.get("id", UUID.class),
                record.get("name", String.class)
        );
    }
}
