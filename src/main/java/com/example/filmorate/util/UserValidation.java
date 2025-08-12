package com.example.filmorate.util;

import com.example.filmorate.exception.UserNotFoundException;
import com.example.filmorate.model.User;

import java.util.Map;

public class UserValidation {
    public static void validateUserExists(Map<Long, User> userMap, long userId) {
        if (!userMap.containsKey(userId))
            throw new UserNotFoundException("User not found: " + userId);
    }
}
