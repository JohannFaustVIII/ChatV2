package org.faust.chat.channel;

import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequestMapping("/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping
    public void addChannel(@RequestBody String name) {
        channelService.addChannel(name);
    }

    @GetMapping
    public Collection<Channel> getChannels() {
        return channelService.getAllChannels();
    }
}
