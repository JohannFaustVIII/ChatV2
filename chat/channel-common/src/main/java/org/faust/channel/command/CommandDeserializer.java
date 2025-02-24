package org.faust.channel.command;

import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UncheckedIOException;

public class CommandDeserializer implements Deserializer<Object> {
    @Override
    public Object deserialize(String topic, byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
