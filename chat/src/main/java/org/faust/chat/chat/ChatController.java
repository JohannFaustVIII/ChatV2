package org.faust.chat.chat;

import org.faust.chat.config.AuthenticationFilter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat/{channel}")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public List<Message> getMessages(@PathVariable("channel") UUID channel) {
        return chatService.getMessages(channel);
    }

    @PostMapping
    public void addMessage(@PathVariable("channel") UUID channel, @RequestBody String message, @AuthenticationPrincipal AuthenticationFilter.AuthUser user) {
        chatService.addMessage(channel, user.getName(), message);
    }
}
