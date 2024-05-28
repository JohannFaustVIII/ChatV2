package org.faust.chat.channel;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    public List<Channel> getAllChannels() {
        return context.selectFrom(DSL.table(SELECT_CHANNEL_TABLE)).fetchInto(Channel.class);
    }

    public boolean existsChannel(UUID channel) {
        return context.fetchExists(
                context
                        .selectOne()
                        .from(DSL.table(DSL.name(INSERT_CHANNEL_TABLE))) // WTF??? Why needs a different way?
                        .where(DSL.field("id", UUID.class).eq(channel))
        );
    }
}
