package org.faust.chat;

import org.faust.chat.command.AddMessage;
import org.faust.chat.command.DeleteMessage;
import org.faust.chat.command.EditMessage;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "CHAT_COMMAND")
public class MessageConsumer {

    @KafkaHandler
    public void addMessage(AddMessage command) {

    }

    @KafkaHandler
    public void editMessage(EditMessage command) {

    }

    @KafkaHandler
    public void deleteMessage(DeleteMessage command) {

    }

}
