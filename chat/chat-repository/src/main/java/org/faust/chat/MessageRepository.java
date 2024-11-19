package org.faust.chat;

import org.faust.chat.exception.WrongOrderException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class MessageRepository {

    private final String SELECT_MESSAGE_TABLE = "\"messageTable\"";
    private final String INSERT_MESSAGE_TABLE = "messageTable";

    private final DSLContext context;

    public MessageRepository(DSLContext context) {
        this.context = context;
    }

    public void addMessage(Message message) {
        context
                .insertInto(DSL.table(DSL.name(INSERT_MESSAGE_TABLE)))
                .set(DSL.field(DSL.name("message")), message.message())
                .set(DSL.field(DSL.name("channelId")), message.channelId())
                .set(DSL.field(DSL.name("sender")), message.sender())
                .set(DSL.field(DSL.name("serverTime")), message.serverTime())
                .set(DSL.field(DSL.name("senderId")), message.senderId())
                .execute();
    }

    public Collection<Message> getAllMessages(UUID channel, UUID before, UUID after, int limit) {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(DSL.field("\"channelId\"", UUID.class).eq(channel));

        if (before != null) {
            conditions.add(
                    DSL.field("\"serverTime\"").lessThan(context
                            .select(DSL.field(DSL.name("serverTime")))
                            .from(SELECT_MESSAGE_TABLE)
                            .where(DSL.field("id").eq(before))
                            .asField())
            );
        }

        if (after != null) {
            conditions.add(
                    DSL.field("\"serverTime\"").greaterThan(context
                            .select(DSL.field(DSL.name("serverTime")))
                            .from(SELECT_MESSAGE_TABLE)
                            .where(DSL.field("id").eq(after))
                            .asField())
            );
        }

        SortField<Object> order = (before != null || after == null) ? DSL.field("\"serverTime\"").desc() : DSL.field("\"serverTime\"").asc();

        List<Message> result = context
                .selectFrom(DSL.table(SELECT_MESSAGE_TABLE))
                .where(conditions)
                .orderBy(order)
                .limit(limit)
                .fetch()
                .map(MessageRepository::mapToMessage);

        if (before != null && after != null && result.isEmpty()) {
            Message beforeMessage = getMessage(before);
            Message afterMessage = getMessage(after);
            if (beforeMessage.serverTime().isBefore(afterMessage.serverTime())) {
                throw new WrongOrderException();
            }
        }

        if (before == null && after != null) {
            Collections.reverse(result);
        }

        return result;
    }

    public void editMessage(UUID messageId, String newMessage) {
        context.update(DSL.table(SELECT_MESSAGE_TABLE))
                .set(DSL.row(DSL.field("\"message\""), DSL.field("\"editTime\"")), DSL.row(newMessage, LocalDateTime.now()))
                .where(
                        DSL.field("\"id\"").eq(messageId)
                ).execute();
    }

    public void deleteMessage(UUID messageId) {
        context.deleteFrom(DSL.table(SELECT_MESSAGE_TABLE))
                .where(
                        DSL.field("\"id\"").eq(messageId)
                ).execute();
    }

    public Message getMessage(UUID messageId) {
        List<Message> messages = context.selectFrom(DSL.table(SELECT_MESSAGE_TABLE))
                .where(
                        DSL.field("\"id\"").eq(messageId)
                ).fetch()
                .map(MessageRepository::mapToMessage);
        return messages.isEmpty() ? null: messages.get(0);
    }

    private static Message mapToMessage(Record record) {
        return new Message(
                record.get("id", UUID.class),
                record.get("channelId", UUID.class),
                record.get("sender", String.class),
                record.get("message", String.class),
                record.get("serverTime", LocalDateTime.class),
                record.get("editTime", LocalDateTime.class),
                record.get("senderId", UUID.class)
        );
    }
}
