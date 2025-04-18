package org.faust.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class MessageSerializer implements Serializer<Message> {
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public byte[] serialize(String topic, Message data) {
        try {
            if (data == null){
                return null;
            }
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing Message to byte[]");
        }
    }
}
