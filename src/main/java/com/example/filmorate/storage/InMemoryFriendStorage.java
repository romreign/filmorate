package com.example.filmorate.storage;

import com.example.filmorate.exception.FriendRequestNotFoundException;
import com.example.filmorate.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.filmorate.util.UserValidation.validateUserExists;

@Component
@AllArgsConstructor
public class InMemoryFriendStorage implements FriendStorage {
    private final InMemoryUserStorage userStorage;
    
    @Override
    public List<User> getFriends(long userId) {
        validateUserExists(userStorage.getUserMap(), userId);
        List<User> friends = new ArrayList<>();

        for (Long key : userStorage.getUserMap().get(userId).getFriends())
            friends.add(userStorage.getUserMap().get(key));
        return friends;
    }

    @Override
    public List<User> getCommonFriends(long userId, long friendId) {
        validateUserExists(userStorage.getUserMap(), userId);
        validateUserExists(userStorage.getUserMap(), friendId);

        Set<Long> friendList1 = userStorage.getUserMap().get(userId).getFriends();
        Set<Long> friendList2 = userStorage.getUserMap().get(friendId).getFriends();
        List<User> commonFriends = new ArrayList<>();

        for (Long key : friendList1)
            if (friendList2.contains(key))
                commonFriends.add(userStorage.getUserMap().get(key));

        return commonFriends;
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        validateUserExists(userStorage.getUserMap(), userId);
        validateUserExists(userStorage.getUserMap(), friendId);
        Set<Long> friends = userStorage.getUserMap().get(userId).getFriends();
        friends.remove(friendId);
        friends = userStorage.getUserMap().get(friendId).getFriends();
        friends.remove(userId);
        return userStorage.getUserMap().get(userId);
    }

    @Override
    public User sendFriendRequest(long userId, long friendId) {
        validateUserExists(userStorage.getUserMap(), userId);
        validateUserExists(userStorage.getUserMap(), friendId);
        userStorage.getUserMap().get(friendId).getFriendRequests().add(userId);
        return userStorage.getUserMap().get(userId);
    }

    @Override
    public User confirmFriend(long userId, long requesterId) {
        validateUserExists(userStorage.getUserMap(), userId);
        validateUserExists(userStorage.getUserMap(), requesterId);

        Set<Long> requests = userStorage.getUserMap().get(userId).getFriendRequests();
        if (!requests.contains(requesterId))
            throw new FriendRequestNotFoundException("Friend request not found");

        userStorage.getUserMap().get(userId).getFriends().add(requesterId);
        userStorage.getUserMap().get(requesterId).getFriends().add(userId);
        requests.remove(requesterId);

        return userStorage.getUserMap().get(userId);
    }

    @Override
    public User rejectFriendRequest(long userId, long requesterId) {
        validateUserExists(userStorage.getUserMap(), userId);
        validateUserExists(userStorage.getUserMap(), requesterId);

        Set<Long> requests = userStorage.getUserMap().get(userId).getFriendRequests();
        if (!requests.contains(requesterId))
            throw new FriendRequestNotFoundException("Friend request not found");

        requests.remove(requesterId);
        return userStorage.getUserMap().get(userId);
    }
}
