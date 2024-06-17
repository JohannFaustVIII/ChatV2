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

    // TODO: this has to be changed into... event stream? Idk, the idea is that each app using this backend can sent user status
    // if one of them RECENTLY sent ONLINE, then the user is ONLINE
    // else if one of them RECENTLY sent AFK, then the user is AFK,
    // else if there is no user status or OFFLINE, then the user is OFFLINE
    // BUT, it needs time evaluation? or remove frontend interval and sent status only if changed, but how to find OFFLINE then?
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
