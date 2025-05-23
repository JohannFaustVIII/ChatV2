package org.faust.chat.user;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;

    private final Sinks.Many<Void> hookSink;

    public UserService(UserRepository repository) {
        this.repository = repository;
        this.hookSink =  Sinks.many().multicast().directBestEffort();
    }

    public void setActive(UUID id, String username) {
        repository.setActive(id, username);
    }

    public void setAfk(UUID id, String username) {
        repository.setAfk(id, username);
    }

    public void setOffline(UUID id, String username) {
        repository.setOffline(id, username);
    }

    public Flux<Void> setActivityHook(UUID userId) {
        repository.incrementUserActivity(userId);
        return this.hookSink.asFlux().doOnCancel(() -> {
            repository.decrementUserActivity(userId);
        });
    }

    public Map<UserStatus, Collection<UserDetails>> getUsers() {
        return this.repository.getUsers();
    }
}
