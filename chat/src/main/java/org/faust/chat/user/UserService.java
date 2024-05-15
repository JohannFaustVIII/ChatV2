package org.faust.chat.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
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

    public List<UserInfo> getActiveUsers() {
        return this.repository.getActiveUsers();
    }
}
