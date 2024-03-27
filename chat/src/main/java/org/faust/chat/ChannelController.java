package org.faust.chat;

import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<Channel> getChannels() {
        return channelService.getAllChannels();
    }
}
