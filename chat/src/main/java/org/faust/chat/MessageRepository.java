package org.faust.chat;

import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public class MessageRepository {

    private List<Message> messages;

    public MessageRepository() {
        this.messages = new LinkedList<>();
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public List<Message> getAllMessages() {
        return messages;
    }
}
