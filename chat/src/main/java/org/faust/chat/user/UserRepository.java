package org.faust.chat.user;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private final Map<UUID, UserInfo> users = new HashMap<>();

    public List<UserInfo> getActiveUsers() {
        LocalDateTime now = LocalDateTime.now().minusMinutes(2);
        return users.values()
                .stream()
                .filter(u -> u.status() != UserStatus.OFFLINE)
                .filter(u -> u.updateTime().isAfter(now))
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
        users.put(id, new UserInfo(
                id, username, status, LocalDateTime.now()
        ));
    }
}
