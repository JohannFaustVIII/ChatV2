package org.faust.chat.external;

import org.faust.chat.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.UUID;

@FeignClient(name = "chat-repository")
@Component
public interface ChatService {
    @GetMapping("/chat/message/{id}")
    Message getMessage(@PathVariable("id") UUID messageId);

    @GetMapping("/chat/{channel}")
    public Collection<Message> getMessages(@PathVariable("channel") UUID channel, @RequestParam(required = false) UUID before, @RequestParam(required = false) UUID after, @RequestParam(defaultValue = "10") int limit);
}
