package com.example.filmorate.storage;

import com.example.filmorate.exception.FriendException;
import com.example.filmorate.model.User;

import java.util.*;

import static com.example.filmorate.util.UserValidation.validateUserExists;

public class InMemoryFriendStorage implements FriendStorage {
    private final Map<Long, User> userMap;

    public InMemoryFriendStorage(Map<Long, User> userMap) {
        this.userMap = userMap;
    }

    public List<User> getFriends(long userId) {
        validateUserExists(userMap, userId);
        List<User> friends = new ArrayList<>();

        for (Long key : userMap.get(userId).getFriends())
            friends.add(userMap.get(key));
        return friends;
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        validateUserExists(userMap, userId);
        validateUserExists(userMap, otherId);

        Set<Long> friendList1 = userMap.get(userId).getFriends();
        Set<Long> friendList2 = userMap.get(otherId).getFriends();
        List<User> commonFriends = new ArrayList<>();

        for (Long key : friendList1)
            if (friendList2.contains(key))
                commonFriends.add(userMap.get(key));

        return commonFriends;
    }

    public User deleteFriend(long userId, long friendId) {
        validateUserExists(userMap, userId);
        validateUserExists(userMap, friendId);

        Set<Long> friends = userMap.get(userId).getFriends();
        friends.remove(friendId);
        return userMap.get(userId);
    }

    public User sendFriendRequest(long userId, long friendId) {
        validateUserExists(userMap, userId);
        validateUserExists(userMap, friendId);

        userMap.get(friendId).getFriendRequests().add(userId);
        return userMap.get(userId);
    }

    public User confirmFriend(long userId, long requesterId) {
        validateUserExists(userMap, userId);
        validateUserExists(userMap, requesterId);

        Set<Long> requests = userMap.get(userId).getFriendRequests();
        if (!requests.contains(requesterId))
            throw new FriendException("Friend request not found");

        userMap.get(userId).getFriends().add(requesterId);
        userMap.get(requesterId).getFriends().add(userId);
        requests.remove(requesterId);

        return userMap.get(userId);
    }

    public User rejectFriendRequest(long userId, long requesterId) {
        validateUserExists(userMap, userId);
        validateUserExists(userMap, requesterId);

        Set<Long> requests = userMap.get(userId).getFriendRequests();
        if (!requests.contains(requesterId))
            throw new FriendException("Friend request not found");

        requests.remove(requesterId);
        return userMap.get(userId);
    }
}
