package org.faust.user.command;

import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;

public class CommandSerializer implements Serializer<Object> {
    @Override
    public byte[] serialize(String topic, Object data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(data);
            return baos.toByteArray();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
