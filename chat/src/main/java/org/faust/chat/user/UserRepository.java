package org.faust.chat.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private final Map<UUID, Cache<UserStatus, UserStatus>> users = new HashMap<>();
    private final Collection<Runnable> listeners = new LinkedList<>();

    public Collection<UserInfo> getActiveUsers() {
        return
                users.entrySet()
                        .stream()
                        .map(UserRepository::mapEntryToUserInfo)
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
                            .removalListener((k, v, cause) -> notifyListeners()) // TODO: removing happens on read, not after real expiration
                            .build()
            );
        }
        users.get(id).put(status, status);
    }

    private static UserInfo mapEntryToUserInfo(Map.Entry<UUID, Cache<UserStatus, UserStatus>> entry) {
        UUID userId = entry.getKey();
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
}
