package org.faust.chat;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public List<Message> getMessages() {
        return chatService.getMessages();
    }

    @PostMapping
    public void addMessage(@RequestBody String message) {
        chatService.addMessage(new Message(
                UUID.randomUUID(),
                message,
                LocalDateTime.now()
        ));
    }
}
