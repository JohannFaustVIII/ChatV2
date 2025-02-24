package org.faust.chat;

import org.faust.user.command.*;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "USER_ACTIVITY")
public class UserConsumer {

    private final UserRepository userRepository;

    public UserConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    }

    @KafkaHandler
    public void setAfk(SetAfk command) {
        userRepository.setAfk(command.userId(), command.username());
    }

    @KafkaHandler
    public void setOffline(SetOffline command) {
        userRepository.setOffline(command.userId(), command.username());
    }
}
