package org.faust.channel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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
    public void addChannel(@RequestBody String name) {
        channelService.addChannel(name);
    }
}
