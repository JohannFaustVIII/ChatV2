package org.faust.channel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// TODO: for E2E to check passing, or at least Kafka container

@RestController
@RequestMapping("/channels")
public class Controller {

    @Value("${spring.kafka.bootstrapServers}")
    private String testProperty;

    private final ChannelService channelService;

    public Controller(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping
    public void addChannel(@RequestHeader("GW_TOKEN_ID") UUID tokenId, @RequestBody String name) {
        channelService.addChannel(tokenId, name);
    }
}
