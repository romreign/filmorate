package com.example.filmorate.storage;

import com.example.filmorate.exception.FriendRequestNotFoundException;
import com.example.filmorate.model.User;

import java.util.*;

import static com.example.filmorate.util.UserValidation.validateUserExists;

public class InMemoryFriendStorage implements FriendStorage {
    private final Map<Long, User> userMap;

    public InMemoryFriendStorage(Map<Long, User> userMap) {
        this.userMap = userMap;
    }

    @Override
    public List<User> getFriends(long userId) {
        validateUserExists(userMap, userId);
        List<User> friends = new ArrayList<>();

        for (Long key : userMap.get(userId).getFriends())
            friends.add(userMap.get(key));
        return friends;
    }

    @Override
    public List<User> getCommonFriends(long userId, long friendId) {
        validateUserExists(userMap, userId);
        validateUserExists(userMap, friendId);

        Set<Long> friendList1 = userMap.get(userId).getFriends();
        Set<Long> friendList2 = userMap.get(friendId).getFriends();
        List<User> commonFriends = new ArrayList<>();

        for (Long key : friendList1)
            if (friendList2.contains(key))
                commonFriends.add(userMap.get(key));

        return commonFriends;
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        validateUserExists(userMap, userId);
        validateUserExists(userMap, friendId);
        Set<Long> friends = userMap.get(userId).getFriends();
        friends.remove(friendId);
        friends = userMap.get(friendId).getFriends();
        friends.remove(userId);
        return userMap.get(userId);
    }

    @Override
    public User sendFriendRequest(long userId, long friendId) {
        validateUserExists(userMap, userId);
        validateUserExists(userMap, friendId);
        userMap.get(friendId).getFriendRequests().add(userId);
        return userMap.get(userId);
    }

    @Override
    public User confirmFriend(long userId, long requesterId) {
        validateUserExists(userMap, userId);
        validateUserExists(userMap, requesterId);

        Set<Long> requests = userMap.get(userId).getFriendRequests();
        if (!requests.contains(requesterId))
            throw new FriendRequestNotFoundException("Friend request not found");

        userMap.get(userId).getFriends().add(requesterId);
        userMap.get(requesterId).getFriends().add(userId);
        requests.remove(requesterId);

        return userMap.get(userId);
    }

    @Override
    public User rejectFriendRequest(long userId, long requesterId) {
        validateUserExists(userMap, userId);
        validateUserExists(userMap, requesterId);

        Set<Long> requests = userMap.get(userId).getFriendRequests();
        if (!requests.contains(requesterId))
            throw new FriendRequestNotFoundException("Friend request not found");

        requests.remove(requesterId);
        return userMap.get(userId);
    }
}
