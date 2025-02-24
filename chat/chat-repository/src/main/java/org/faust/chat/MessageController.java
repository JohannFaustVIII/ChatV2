package org.faust.chat;

import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

// TODO: E2E test, to think later, needs kafka

@RestController
@RequestMapping("/chat")
public class MessageController {

    private final MessageRepository repository;

    public MessageController(MessageRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/message/{id}")
    public Message getMessage(@PathVariable("id") UUID messageId) {
        return repository.getMessage(messageId);
    }

    @GetMapping("/{channel}")
    public Collection<Message> getMessages(@PathVariable("channel") UUID channel, @RequestParam(required = false) UUID before, @RequestParam(required = false) UUID after, @RequestParam(defaultValue = "10") int limit) {
        return repository.getAllMessages(channel, before, after, limit);
    }
}
