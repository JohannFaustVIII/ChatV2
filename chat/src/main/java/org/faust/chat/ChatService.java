package org.faust.chat;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final MessageRepository messageRepository;

    public ChatService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void addMessage(Message message) {
        messageRepository.addMessage(message);
    }

    public List<Message> getMessages() {
        return messageRepository.getAllMessages();
    }

}
