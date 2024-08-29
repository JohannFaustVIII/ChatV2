package org.faust.chat.chat;

import org.jooq.Condition;
import org.jooq.DSLContext;
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
                .execute();
    }

    // TODO: how to test the code below?
    // So, the whole app would require setting up: keycloak and a database, then using liquibase
    // Testing just repository, would require a database, but liquibase might be harder
    // To think more https://stackoverflow.com/questions/43523971/how-to-set-up-liquibase-in-spring-for-multiple-data-sources
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
                .fetch()
                .map(Message::mapToMessage);
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
                .map(Message::mapToMessage);
        return messages.isEmpty() ? null: messages.get(0);
    }
}
