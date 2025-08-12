package com.example.filmorate.service;

import com.example.filmorate.exception.FriendException;
import com.example.filmorate.model.User;
import com.example.filmorate.storage.FriendStorage;
import com.example.filmorate.storage.UserStorage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserStorage userStorage;
    private FriendStorage friendStorage;

    private void validateUserExists(long userId, long friendId, String message) {
        if (userId == friendId)
            throw new FriendException(message);
    }

    public UserService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(long userId) {
        return userStorage.getUserById(userId);
    }

    public List<User> getFriends(long userId) {
        return friendStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        return friendStorage.getCommonFriends(userId, otherId);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User sendFriendRequest(long userId, long friendId) {
        validateUserExists(userId, friendId, "You cannot send a friend request to yourself");
        return friendStorage.sendFriendRequest(userId, friendId);
    }

    public User updateUser(long userId, User user) {
        return userStorage.updateUser(userId, user);
    }

    public User deleteFriend(long userId, long friendId) {
        validateUserExists(userId, friendId,"You can't remove yourself from friends");
        return friendStorage.deleteFriend(userId, friendId); 
    }

    public User confirmFriend(long userId, long requesterId) {
        validateUserExists(userId, requesterId, "You can't accept yourself as a friend");
        return friendStorage.confirmFriend(userId, requesterId);
    }

    public User rejectFriendRequest(long userId, long requesterId) {
        validateUserExists(userId, requesterId, "You can't reject yourself");
        return friendStorage.rejectFriendRequest(userId, requesterId);
    }
}
