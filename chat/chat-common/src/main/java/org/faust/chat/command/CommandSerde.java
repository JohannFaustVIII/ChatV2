package org.faust.chat.command;

import org.apache.kafka.common.serialization.Serdes;

public class CommandSerde extends Serdes.WrapperSerde<Object> {


    public CommandSerde() {
        super(new CommandSerializer(), new CommandDeserializer());
    }
}
