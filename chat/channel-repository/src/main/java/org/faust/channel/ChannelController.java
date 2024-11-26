package org.faust.channel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/channel")
public class ChannelController {

    private final ChannelRepository repository;

    public ChannelController(ChannelRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/exists/{id}")
    public boolean existsChannel(@PathVariable("id") UUID channelId) {
        return repository.existsChannelWithId(channelId);
    }
}

