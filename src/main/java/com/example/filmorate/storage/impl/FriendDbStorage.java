package com.example.filmorate.storage.impl;

import com.example.filmorate.exception.FriendRequestNotFoundException;
import com.example.filmorate.exception.FriendshipIdConflictException;
import com.example.filmorate.exception.UserNotFoundException;
import com.example.filmorate.model.User;
import com.example.filmorate.storage.FriendStorage;
import com.example.filmorate.storage.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public List<User> getFriends(long userId) {
        String sql = """
            SELECT u.* 
            FROM users u
            JOIN friendships f ON u.id = f.friend_id
            WHERE f.user_id = ? AND f.status = 'CONFIRMED'
            ORDER BY u.id
            """;

        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        String sql = """
            SELECT u.* 
            FROM users u
            JOIN friendships f1 ON u.id = f1.friend_id
            JOIN friendships f2 ON u.id = f2.friend_id
            WHERE f1.user_id = ? AND f2.user_id = ? 
            AND f1.status = 'CONFIRMED' AND f2.status = 'CONFIRMED'
            ORDER BY u.id
            """;

        return jdbcTemplate.query(sql, userRowMapper, userId, otherId);
    }

    @Override
    public User sendFriendRequest(long userId, long friendId) {
        String sql = """ 
                INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, 'PENDING')
                """;

        try {
            jdbcTemplate.update(sql, friendId, userId);
            log.info("Friend request sent from user {} to user {}", userId, friendId);
        } catch (DuplicateKeyException e) {
            log.warn("Friendship already exists between user {} and user {}", userId, friendId);
            throw new FriendshipIdConflictException("Friendship already exists between user "
                    + userId + " and user " + friendId);
        }

        return getUserById(userId);
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        String sql = """ 
                DELETE FROM friendships 
                WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)
                """;

        int deletedRows = jdbcTemplate.update(sql, userId, friendId, friendId, userId);

        if (deletedRows > 0)
            log.info("Friendship between user {} and user {} deleted", userId, friendId);
        else {
            log.warn("Friendship not found between user {} and user {}", userId, friendId);
            throw new FriendshipIdConflictException("Friendship not found between user "
                    + userId + " and user " + friendId);
        }

        return getUserById(userId);
    }

    @Override
    public User confirmFriend(long userId, long requesterId) {
        String sql = """
            UPDATE friendships 
            SET status = 'CONFIRMED' 
            WHERE user_id = ? AND friend_id = ? AND status = 'PENDING'
            """;

        int updatedRows = jdbcTemplate.update(sql, userId, requesterId);

        if (updatedRows == 0)
            throw new FriendRequestNotFoundException("Pending friend request not found from user "
                    + requesterId + " to user " + userId);

        log.info("User {} confirmed friend request from user {}", userId, requesterId);
        return getUserById(userId);
    }

    @Override
    public User rejectFriendRequest(long userId, long requesterId) {
        String sql = """
                DELETE FROM friendships 
                WHERE user_id = ? AND friend_id = ? AND status = 'PENDING'
                """;

        int deletedRows = jdbcTemplate.update(sql, userId, requesterId);

        if (deletedRows == 0)
            throw new FriendRequestNotFoundException("Pending friend request not found from user "
                    + requesterId + " to user " + userId);

        log.info("User {} rejected friend request from user {}", userId, requesterId);
        return getUserById(userId);
    }

    private User getUserById(long userId) {
        String sql = """ 
            SELECT 
            u.*,
            STRING_AGG(DISTINCT CASE WHEN f.status = 'CONFIRMED' THEN CAST(f.friend_id AS TEXT) END, ',') AS friend_ids,
            STRING_AGG(DISTINCT CASE WHEN f.status = 'PENDING' AND f.friend_id = u.id THEN CAST(f.user_id AS TEXT) END, ',') AS incoming_request_ids,
            STRING_AGG(DISTINCT CASE WHEN f.status = 'PENDING' AND f.user_id = u.id THEN CAST(f.friend_id AS TEXT) END, ',') AS outgoing_request_idsFROM users u LEFT JOIN friendships f ON u.id = f.user_id OR u.id = f.friend_id WHERE u.id = ? GROUP BY u.id
            """;

        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
    }
}
