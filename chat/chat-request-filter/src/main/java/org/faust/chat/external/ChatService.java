package org.faust.chat.external;

import org.faust.chat.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

//@FeignClient(name = "chat-repository") //TODO: to interface?
@Component
public class ChatService {
    public Message getMessage(UUID messasgeId) {
        return null;
    }
}
