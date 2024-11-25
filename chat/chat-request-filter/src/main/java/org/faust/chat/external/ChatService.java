package org.faust.chat.external;

import org.faust.chat.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "chat-repository")
@Component
public interface ChatService {
    @GetMapping("/chat/message/{id}")
    Message getMessage(@PathVariable("id") UUID messageId);
}
