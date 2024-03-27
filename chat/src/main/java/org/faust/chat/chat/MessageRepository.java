package org.faust.chat.chat;

import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Repository
public class MessageRepository {

    private List<Message> messages;

    public MessageRepository() {
        this.messages = new LinkedList<>();
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public List<Message> getAllMessages(UUID channel) {
        return messages.stream().filter(m -> m.channelId().equals(channel)).toList();
    }
}
