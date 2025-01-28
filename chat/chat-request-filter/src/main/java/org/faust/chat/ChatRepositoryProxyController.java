package org.faust.chat;

import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

// TODO: E2E test? or some integration to skip kafka, to think later

@RestController
@RequestMapping("/chat")
public class ChatRepositoryProxyController {

    private final ChatRepositoryProxyService chatRepositoryProxyService;

    public ChatRepositoryProxyController(ChatRepositoryProxyService chatRepositoryProxyService) {
        this.chatRepositoryProxyService = chatRepositoryProxyService;
    }

    @GetMapping("/{channel}")
    public Collection<Message> getMessages(@PathVariable("channel") UUID channel, @RequestParam(required = false) UUID before, @RequestParam(required = false) UUID after, @RequestParam(defaultValue = "10") int limit) {
        return chatRepositoryProxyService.getAllMessages(channel, before, after, limit);
    }
}
