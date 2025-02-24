package org.faust.channel;

import java.io.Serializable;
import java.util.UUID;

public record Channel(UUID id, String name) implements Serializable {

}
