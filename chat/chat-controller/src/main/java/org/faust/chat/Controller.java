package org.faust.chat;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/chat/{channel}")
public class Controller {

    private final ChatService chatService;

    public Controller(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public void addMessage(@PathVariable("channel") UUID channel, @RequestBody String message, @RequestHeader("GW_USER") String username, @RequestHeader("GW_USER_ID") UUID userId, @RequestHeader("GW_TOKEN_ID") UUID tokenId) {
        chatService.addMessage(channel, username, userId, tokenId, message);
    }

    @DeleteMapping("/{id}")
    public void deleteMessage(@PathVariable("channel") UUID channel, @PathVariable("id") UUID messageId, @RequestHeader("GW_USER_ID") UUID userId, @RequestHeader("GW_TOKEN_ID") UUID tokenId) {
        chatService.deleteMessage(channel, messageId, tokenId, userId);
    }

    @PutMapping("/{id}")
    public void editMessage(@PathVariable("channel") UUID channel, @PathVariable("id") UUID messageId, @RequestBody String newMessage, @RequestHeader("GW_USER_ID") UUID userId, @RequestHeader("GW_TOKEN_ID") UUID tokenId) {
        chatService.editMessage(channel, messageId, userId, tokenId, newMessage);
    }
}
