package com.example.filmorate.storage;

import com.example.filmorate.exception.EmailAlreadyExistsException;
import com.example.filmorate.model.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.filmorate.util.UserValidation.validateUserExists;

@Component
public class InMemoryUserStorage implements UserStorage {
    @Getter
    private final Map<Long, User> userMap = new HashMap<>();
    private final Map<String, Long> emailToIdMap = new HashMap<>();
    private long autoId = 1;

    private void incId() {
        ++autoId;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User getUserById(long userId) {
        validateUserExists(userMap, userId);
        return userMap.get(userId);
    }

    @Override
    public User createUser(User user) {
        user.setId(autoId);
        userMap.put(autoId, user);
        emailToIdMap.put(user.getEmail(), autoId);
        incId();
        return user;
    }

    @Override
    public User updateUser(long userId, User newUser) {
        validateUserExists(userMap, userId);
        if (emailToIdMap.containsKey(newUser.getEmail()))
            throw new EmailAlreadyExistsException("Email already exists");

        if (!userMap.get(userId).getEmail().equals(newUser.getEmail()))
            emailToIdMap.put(newUser.getEmail(), userId);
        userMap.put(userId, newUser);
        return newUser;
    }
}
