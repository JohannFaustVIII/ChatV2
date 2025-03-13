package org.faust.user;

import org.faust.user.command.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.UUID;

@Service
public class UserService {

    private static final String ACTIVITY_TOPIC = "USER_ACTIVITY";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void setActive(UUID id, String username) {
        kafkaTemplate.send(ACTIVITY_TOPIC, id.toString(), new SetOnline(id, username));
    }

    public void setAfk(UUID id, String username) {
        kafkaTemplate.send(ACTIVITY_TOPIC, id.toString(), new SetAfk(id, username));
    }

    public void setOffline(UUID id, String username) {
        kafkaTemplate.send(ACTIVITY_TOPIC, id.toString(), new SetOffline(id, username));
    }

    public Flux<String> setActivityHook(UUID userId) {
        // TODO: Change it to not send "ping" every 30 seconds and keep the hook working
        kafkaTemplate.send(ACTIVITY_TOPIC, userId.toString(), new IncreaseHook(userId));
        return Flux.interval(Duration.ofSeconds(30)).map(l -> "ping").doOnCancel(() -> {
            kafkaTemplate.send(ACTIVITY_TOPIC, userId.toString(), new DecreaseHook(userId));
        });
    }
}
