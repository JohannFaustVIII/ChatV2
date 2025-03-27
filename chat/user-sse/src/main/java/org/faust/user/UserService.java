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

    private final Sinks.Many<String> processor;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.processor =  Sinks.many().replay().all();
        this.processor.tryEmitNext("ping");
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
        kafkaTemplate.send(ACTIVITY_TOPIC, userId.toString(), new IncreaseHook(userId));
        return this.processor.asFlux().doOnCancel(() -> {
            kafkaTemplate.send(ACTIVITY_TOPIC, userId.toString(), new DecreaseHook(userId));
        });
    }
}
