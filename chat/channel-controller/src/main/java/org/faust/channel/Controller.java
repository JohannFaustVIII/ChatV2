package org.faust.channel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/channels")
public class Controller {

    @Value("${spring.kafka.bootstrapServers}")
    private String testProperty;

    private final ChannelService channelService;

    public Controller(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping
    @RequestMapping("/get")
    public String getChannel(@RequestHeader Map<String, String> headers) {
        return null;
    }

    @PostMapping
    public void addChannel(@RequestBody String name) {
        channelService.addChannel(name);
    }

    @GetMapping
    public Collection<Channel> getChannels() {
        // TODO: it feels like CQRS should get implemented, and one controller should be for commands, and another for queries. or repository should handle queries?
//        return channelService.getAllChannels();
        return null;
    }
}
