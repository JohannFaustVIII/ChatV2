package org.faust.channel.command;

import org.faust.channel.Channel;

import java.io.Serializable;
import java.util.UUID;

public record AddChannel(UUID tokenId, Channel channel) implements Serializable {
}
