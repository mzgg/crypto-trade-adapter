package com.crypto.adapter.out.db;

import com.crypto.application.domain.model.User;
import com.crypto.application.port.out.UserPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserDataAdapter implements UserPort {

    private static final Map<Long, User> userStore = new HashMap<>();

    static {
        String rawData = System.getenv("USER_DATA");
        if (rawData != null) {
            String[] entries = rawData.split(",");
            for (String entry : entries) {
                String[] parts = entry.split(":");
                if (parts.length == 4) {
                    try {
                        Long id = Long.parseLong(parts[0]);
                        String name = parts[1];
                        String accessKey = parts[2];
                        String secretKey = parts[3];
                        userStore.put(id, new User(id, name, accessKey, secretKey));
                    } catch (NumberFormatException e) {
                        log.error("Invalid user ID format in USER_DATA: {}", parts[0]);
                    }
                }
            }
        }
    }

    @Override
    public User findByUserById(Long userId) {
        User user = userStore.get(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        log.info("user found userId: {}, userName:{}", user.getId(), user.getName());
        return user;
    }
}
