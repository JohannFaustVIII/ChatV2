package org.faust.chat.stream;

import org.faust.chat.command.AddMessage;
import org.faust.chat.command.DeleteMessage;
import org.faust.chat.command.EditMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;

@Configuration
public class MessageFilterStream {
    public final static String INPUT_TOPIC = "CHAT_REQUEST";
    public final static String OUTPUT_TOPIC = "CHAT_COMMAND";

    @Bean
    public KStream<String, Object> requestFilter(StreamsBuilder streamsBuilder) {
        KStream<String, Object> input = streamsBuilder.stream(INPUT_TOPIC);
        KStream<String, Object> output = input
                .filter((key, value) -> switch(value) {
                        case AddMessage add -> filter(add);
                        case DeleteMessage delete -> filter(delete);
                        case EditMessage edit -> filter(edit);
                        default -> false; //TODO: implement
                    }
                );

        output.to(OUTPUT_TOPIC);
        return output;
    }

    public boolean filter(AddMessage command) {
        return true;
    }

    public boolean filter(DeleteMessage command) {
        return true;
    }

    public boolean filter(EditMessage command) {
        return true;
    }


}
