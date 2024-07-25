package org.faust.chat.channel;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
@Scope("singleton")
public class ChannelRepository {

    private final String SELECT_CHANNEL_TABLE = "\"channelTable\"";
    private final String INSERT_CHANNEL_TABLE = "channelTable";

    private final DSLContext context;

    public ChannelRepository(DSLContext context) {
        this.context = context;
    }

    public void addChannel(Channel channel) {
        context
                .insertInto(DSL.table(DSL.name(INSERT_CHANNEL_TABLE)))
                .set(DSL.field(DSL.name("name")), channel.name())
                .execute();
    }

    public Collection<Channel> getAllChannels() {
        return context.select().from(DSL.table(SELECT_CHANNEL_TABLE))
                .fetch()
                .map(mapToChannel());
    }

    public boolean existsChannel(UUID channel) {
        return context.fetchExists(
                context
                        .selectOne()
                        .from(DSL.table(DSL.name(INSERT_CHANNEL_TABLE))) // WTF??? Why needs a different way?
                        .where(DSL.field("id", UUID.class).eq(channel))
        );
    }

    private static RecordMapper<Record, Channel> mapToChannel() {
        return record -> new Channel(
                record.get("id", UUID.class),
                record.get("name", String.class)
        );
    }
}
