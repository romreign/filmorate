package com.example.filmorate.storage;

import com.example.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    List<User> getFriends(long userId);
    List<User> getCommonFriends(long userId, long otherId);
    User sendFriendRequest(long userId, long friendId);
    User deleteFriend(long userId, long friendId);
    User confirmFriend(long userId, long requesterId);
    User rejectFriendRequest(long userId, long requesterId);
}
