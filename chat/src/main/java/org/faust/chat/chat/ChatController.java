package org.faust.chat.chat;

import org.faust.chat.config.AuthUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/chat/{channel}")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public Collection<Message> getMessages(@PathVariable("channel") UUID channel, @RequestParam(required = false) UUID before, @RequestParam(required = false) UUID after, @RequestParam(defaultValue = "10") int limit) {
        return chatService.getMessages(channel, before, after, limit);
    }

    @PostMapping
    public void addMessage(@PathVariable("channel") UUID channel, @RequestBody String message, @AuthenticationPrincipal AuthUser user) {
        chatService.addMessage(channel, user.getName(), message);
    }

    @DeleteMapping("/{id}")
    public void deleteMessage(@PathVariable("channel") UUID channel, @PathVariable("id") UUID messageId, @AuthenticationPrincipal AuthUser user) {
        chatService.deleteMessage(channel, messageId, user.getName());
    }

    @PutMapping("/{id}")
    public void editMessage(@PathVariable("channel") UUID channel, @PathVariable("id") UUID messageId, @RequestBody String newMessage, @AuthenticationPrincipal AuthUser user) {
        chatService.editMessage(channel, messageId, user.getName(), newMessage);
    }
}
