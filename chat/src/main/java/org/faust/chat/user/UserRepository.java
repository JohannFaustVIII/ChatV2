package org.faust.chat.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private final Map<UUID, Cache<UserStatus, UserStatus>> users = new HashMap<>();
    private final Map<UUID, AtomicInteger> activityCounters = new ConcurrentHashMap<>();
    private final Collection<Runnable> listeners = new LinkedList<>();

    public Collection<UserInfo> getActiveUsers() {
        return
                users.entrySet()
                        .stream()
                        .map(this::mapEntryToUserInfo)
                        .collect(Collectors.toList());
    }

    public void setActive(UUID id, String username) {
        setStatus(id, username, UserStatus.ONLINE);
    }

    public void setAfk(UUID id, String username) {
        setStatus(id, username, UserStatus.AFK);
    }

    public void setOffline(UUID id, String username) {
        setStatus(id, username, UserStatus.OFFLINE);
    }

    private void setStatus(UUID id, String username, UserStatus status) {
        if (!users.containsKey(id)) {
            users.put(
                    id,
                    Caffeine.newBuilder()
                            .expireAfterWrite(70, TimeUnit.SECONDS)
                            .removalListener((k, v, cause) -> notifyListeners())
                            .scheduler(Scheduler.systemScheduler())
                            .build()
            );
        }
        users.get(id).put(status, status);
    }

    private UserInfo mapEntryToUserInfo(Map.Entry<UUID, Cache<UserStatus, UserStatus>> entry) {
        UUID userId = entry.getKey();

        AtomicInteger userCounter = activityCounters.get(userId);
        if (userCounter == null || userCounter.get() == 0) {
            return new UserInfo(userId, UserStatus.OFFLINE);
        }

        Cache<UserStatus, UserStatus> cache = entry.getValue();
        UserStatus status = cache.getIfPresent(UserStatus.ONLINE);
        if (null == status) {
            status = cache.getIfPresent(UserStatus.AFK);
        }
        if (null == status) {
            status = UserStatus.OFFLINE;
        }
        return new UserInfo(userId, status);
    }

    public void addListener(Runnable r) {
        listeners.add(r);
    }

    private void notifyListeners() {
        for (Runnable r: listeners) {
            r.run();
        }
    }

    public void incrementUserActivity(UUID userId) {
        activityCounters.computeIfAbsent(userId, id -> new AtomicInteger(0)).incrementAndGet();
    }

    public void decrementUserActivity(UUID userId) {
        activityCounters.get(userId).decrementAndGet();
    }
}
