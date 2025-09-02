package com.example.filmorate.storage;

import com.example.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getUsers();
    User createUser(User user);
    User updateUser(long userId, User newUser);
    Optional<User> getUserById(long userId);
}
