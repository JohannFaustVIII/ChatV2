package org.faust.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;

public class ChannelDeserializer implements Deserializer<Channel> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Channel deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                return null;
            }
            return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), Channel.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to Channel");
        }
    }
}
