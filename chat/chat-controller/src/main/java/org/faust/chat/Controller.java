package org.faust.chat;

import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/chat/{channel}")
public class Controller {

    private final ChatService chatService;

    public Controller(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public Collection<Message> getMessages(@PathVariable("channel") UUID channel, @RequestParam(required = false) UUID before, @RequestParam(required = false) UUID after, @RequestParam(defaultValue = "10") int limit) {
        return chatService.getMessages(channel, before, after, limit); //TODO: move to repo?
    }

    @PostMapping
    public void addMessage(@PathVariable("channel") UUID channel, @RequestBody String message, @RequestHeader("GW_USER") String username, @RequestHeader("GW_USER_ID") UUID userId) {
        chatService.addMessage(channel, username, userId, message);
    }

    @DeleteMapping("/{id}")
    public void deleteMessage(@PathVariable("channel") UUID channel, @PathVariable("id") UUID messageId, @RequestHeader("GW_USER_ID") UUID userId) {
        chatService.deleteMessage(channel, messageId, userId);
    }

    @PutMapping("/{id}")
    public void editMessage(@PathVariable("channel") UUID channel, @PathVariable("id") UUID messageId, @RequestBody String newMessage, @RequestHeader("GW_USER_ID") UUID userId) {
        chatService.editMessage(channel, messageId, userId, newMessage);
    }
}
