package org.faust.chat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping
    public void addChannel(String name) {
        channelService.addChannel(name);
    }

    @GetMapping
    public List<Channel> getChannels() {
        return channelService.getAllChannels();
    }
}
