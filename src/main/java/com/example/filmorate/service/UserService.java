package com.example.filmorate.service;

import com.example.filmorate.exception.FriendRequestNotFoundException;
import com.example.filmorate.exception.FriendshipIdConflictException;
import com.example.filmorate.model.User;
import com.example.filmorate.storage.FriendStorage;
import com.example.filmorate.storage.UserStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.filmorate.util.IdValidation.validateIdExists;

@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    private void validateUserExists(long userId, long friendId, String message) {
        if (userId == friendId)
            throw new FriendRequestNotFoundException(message);
    }

    private void validateFriendshipIds(long userId, long friendId) {
        if (userId == friendId)
            throw new FriendshipIdConflictException("User id cannot be the same as friend id");
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(long userId) {
        validateIdExists(userId);
        return userStorage.getUserById(userId);
    }

    public List<User> getFriends(long userId) {
        validateIdExists(userId);
        return friendStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        validateFriendshipIds(userId, friendId);
        validateIdExists(userId);
        validateIdExists(friendId);
        return friendStorage.getCommonFriends(userId, friendId);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User sendFriendRequest(long userId, long friendId) {
        validateUserExists(userId, friendId, "You cannot send a friend request to yourself");
        validateIdExists(userId);
        validateIdExists(friendId);
        return friendStorage.sendFriendRequest(userId, friendId);
    }

    public User updateUser(long userId, User user) {
        validateIdExists(userId);
        return userStorage.updateUser(userId, user);
    }

    public User deleteFriend(long userId, long friendId) {
        validateUserExists(userId, friendId,"You can't remove yourself from friends");
        validateIdExists(userId);
        validateIdExists(friendId);
        return friendStorage.deleteFriend(userId, friendId);
    }

    public User confirmFriend(long userId, long requesterId) {
        validateUserExists(userId, requesterId, "You can't accept yourself as a friend");
        validateIdExists(userId);
        return friendStorage.confirmFriend(userId, requesterId);
    }

    public User rejectFriendRequest(long userId, long requesterId) {
        validateUserExists(userId, requesterId, "You can't reject yourself");
        validateIdExists(userId);
        validateIdExists(requesterId);
        return friendStorage.rejectFriendRequest(userId, requesterId);
    }
}
