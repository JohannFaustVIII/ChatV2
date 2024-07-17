package org.faust.chat.chat;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

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
        return context
                .selectFrom(DSL.table(SELECT_MESSAGE_TABLE))
                .where(conditions)
                .orderBy(DSL.field("\"serverTime\"").desc())
                .limit(limit)
                .fetchInto(Message.class);
    }

    public void editMessage(UUID channel, UUID messageId, String sender, String newMessage) {
        if (getMessage(channel, messageId, sender) == null) {
            throw new RuntimeException("Message not found.");
        }
        context.update(DSL.table(SELECT_MESSAGE_TABLE))
                .set(DSL.row(DSL.field("\"message\"")), DSL.row(newMessage))
                .where(
                        DSL.field("\"id\"").eq(messageId),
                        DSL.field("\"channelId\"").eq(channel),
                        DSL.field("\"sender\"").eq(sender)
                ).execute();
    }

    public void deleteMessage(UUID channel, UUID messageId, String sender) {
        if (getMessage(channel, messageId, sender) == null) {
            throw new RuntimeException("Message not found.");
        }
        context.deleteFrom(DSL.table(SELECT_MESSAGE_TABLE))
                .where(
                        DSL.field("\"id\"").eq(messageId),
                        DSL.field("\"channelId\"").eq(channel),
                        DSL.field("\"sender\"").eq(sender))
                .execute();
    }

    private Message getMessage(UUID channel, UUID messageId, String sender) {
        List<Message> messages = context.selectFrom(DSL.table(SELECT_MESSAGE_TABLE))
                .where(
                        DSL.field("\"id\"").eq(messageId),
                        DSL.field("\"channelId\"").eq(channel),
                        DSL.field("\"sender\"").eq(sender)
                ).fetchInto(Message.class);
        return messages.isEmpty() ? null: messages.get(0);
    }
}
