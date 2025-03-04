package org.faust.chat;

import org.faust.user.command.*;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "USER_ACTIVITY")
public class UserConsumer {

    public final static String SSE_TOPIC = "SSE_EVENTS";
    private final UserRepository userRepository;
    private final KafkaTemplate<String, org.faust.sse.Message> kafkaTemplate;

    public UserConsumer(UserRepository userRepository, KafkaTemplate<String, org.faust.sse.Message> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;

        this.userRepository.addListener(this::notifyAboutUsers);
    }

    @KafkaHandler
    public void increaseActivity(IncreaseHook command) {
        userRepository.incrementUserActivity(command.userId());
    }

    @KafkaHandler
    public void decreaseActivity(DecreaseHook command) {
        userRepository.decrementUserActivity(command.userId());
    }

    @KafkaHandler
    public void setOnline(SetOnline command) {
        userRepository.setActive(command.userId(), command.username());
        this.notifyAboutUsers();
    }

    @KafkaHandler
    public void setAfk(SetAfk command) {
        userRepository.setAfk(command.userId(), command.username());
        this.notifyAboutUsers();
    }

    @KafkaHandler
    public void setOffline(SetOffline command) {
        userRepository.setOffline(command.userId(), command.username());
        this.notifyAboutUsers();
    }

    private void notifyAboutUsers() {
        kafkaTemplate.send(SSE_TOPIC, org.faust.sse.Message.globalNotify("users"));
    }
}
