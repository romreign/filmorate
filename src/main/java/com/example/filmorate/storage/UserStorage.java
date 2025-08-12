package com.example.filmorate.storage;

import com.example.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();
    User createUser(User user);
    User updateUser(long userId, User newUser);
    User getUserById(long userId);
}
