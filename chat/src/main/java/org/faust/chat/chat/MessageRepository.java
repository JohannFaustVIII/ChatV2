package org.faust.chat.chat;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MessageRepository {

    private final List<Message> messages;

    public MessageRepository() {
        this.messages = new LinkedList<>();
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public List<Message> getAllMessages(UUID channel, UUID before, UUID after, int limit) {
        List<Message> channelMessages = messages.stream().filter(m -> m.channelId().equals(channel)).toList();
        List<UUID> ids = channelMessages.stream().map(Message::id).toList();;
        int beforeSkip = channelMessages.size();
        if (before != null) {
            beforeSkip = ids.indexOf(before);
            if (beforeSkip == -1) {
                beforeSkip = channelMessages.size();
            }
        }

        int afterSkip = 0;
        if (after != null) {
            afterSkip = ids.indexOf(after) + 1;
        }

        List<Message> messagesInRange = channelMessages.stream().limit(beforeSkip).skip(afterSkip).toList();
        List<Message> reversedResult =  messagesInRange.stream().skip(Math.max(0, messagesInRange.size() - limit)).toList();
        List<Message> result = new ArrayList<>(reversedResult.size());
        result.addAll(reversedResult);
        Collections.reverse(result);
        return result;
    }
}
