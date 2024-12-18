package org.faust.chat;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class UserRepository {

    private final Map<UUID, Cache<UserStatus, UserStatus>> users = new HashMap<>();
    private final Map<UUID, AtomicInteger> activityCounters = new ConcurrentHashMap<>();
    private final Collection<Runnable> listeners = new LinkedList<>();

//    private final KeycloakRepository keycloakRepository;

    public UserRepository() {
//        this.keycloakRepository = keycloakRepository;
    }

//    public Map<UserStatus, Collection<UserDetails>> getUsers() {
//        Map<UserStatus, Collection<UserDetails>> result = initStatusMap();
//
//        keycloakRepository
//                .getUsers()
//                .stream()
//                .map(this::mapUserDetailsToEntry)
//                .forEach(entry -> result.get(entry.getValue()).add(entry.getKey()));
//        return result;
//    }

//    private static Map<UserStatus, Collection<UserDetails>> initStatusMap() {
//        UserStatus[] statuses = UserStatus.values();
//        Map<UserStatus, Collection<UserDetails>> result = new HashMap<>(statuses.length);
//        Arrays.stream(statuses).forEach(status -> result.put(status, new LinkedList<>()));
//        return result;
//    }

//    private AbstractMap.SimpleEntry<UserDetails, UserStatus> mapUserDetailsToEntry(UserDetails details) {
//        Cache<UserStatus, UserStatus> cache = users.get(details.id());
//        UserStatus status;
//        if (cache == null) {
//            status = UserStatus.OFFLINE;
//        } else {
//            status = mapEntryToUserInfo(details.id(), cache);
//        }
//        return new AbstractMap.SimpleEntry<UserDetails, UserStatus>(details, status);
//    }

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

    private UserStatus mapEntryToUserInfo(UUID userId, Cache<UserStatus, UserStatus> cache) {
        AtomicInteger userCounter = activityCounters.get(userId);
        if (userCounter == null || userCounter.get() == 0) {
            return UserStatus.OFFLINE;
        }

        UserStatus status = cache.getIfPresent(UserStatus.ONLINE);
        if (null == status) {
            status = cache.getIfPresent(UserStatus.AFK);
        }
        if (null == status) {
            status = UserStatus.OFFLINE;
        }
        return status;
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
