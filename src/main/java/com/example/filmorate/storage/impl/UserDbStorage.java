package com.example.filmorate.storage.impl;

import com.example.filmorate.exception.UserNotFoundException;
import com.example.filmorate.model.User;
import com.example.filmorate.storage.UserStorage;
import com.example.filmorate.storage.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public List<User> getUsers() {
        String sql = """ 
            SELECT 
                u.*,
                STRING_AGG(DISTINCT CASE WHEN f.status = 'CONFIRMED' THEN CAST(f.friend_id AS TEXT) END, ',') AS friend_ids,
                STRING_AGG(DISTINCT CASE WHEN f.status = 'PENDING' AND f.friend_id = u.id THEN CAST(f.user_id AS TEXT) END, ',') AS incoming_request_ids,
                STRING_AGG(DISTINCT CASE WHEN f.status = 'PENDING' AND f.user_id = u.id THEN CAST(f.friend_id AS TEXT) END, ',') AS outgoing_request_ids
            FROM users u
            LEFT JOIN friendships f ON u.id = f.user_id OR u.id = f.friend_id
            GROUP BY u.id
            ORDER BY u.id
            """;
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = """
                       INSERT INTO users (login, name, email, birthday)
                       VALUES (?, ?, ?, ?)
                       """;

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User updateUser(long userId, User newUser) {
        String sql = """
                UPDATE users SET login = ?, name = ?, email = ?, birthday = ? 
                WHERE id = ?
                """;

        int updatedRows = jdbcTemplate.update(sql,
                newUser.getLogin(),
                newUser.getName(),
                newUser.getEmail(),
                java.sql.Date.valueOf(newUser.getBirthday()),
                userId);

        if (updatedRows == 0)
            throw new UserNotFoundException("User with id " + userId + " not found");

        newUser.setId(userId);
        return newUser;
    }

    @Override
    public Optional<User> getUserById(long userId) {
        String sql = """ 
            SELECT 
                u.*,
                STRING_AGG(DISTINCT CASE WHEN f.status = 'CONFIRMED' THEN CAST(f.friend_id AS TEXT) END, ',') AS friend_ids,
                STRING_AGG(DISTINCT CASE WHEN f.status = 'PENDING' AND f.friend_id = u.id THEN CAST(f.user_id AS TEXT) END, ',') AS incoming_request_ids,
                STRING_AGG(DISTINCT CASE WHEN f.status = 'PENDING' AND f.user_id = u.id THEN CAST(f.friend_id AS TEXT) END, ',') AS outgoing_request_ids
            FROM users u
            LEFT JOIN friendships f ON u.id = f.user_id OR u.id = f.friend_id
            WHERE u.id = ?
            GROUP BY u.id
            """;

        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, userId);
            assert user != null;
            log.info("User with id {} found: {}", userId, user.getEmail());
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            log.info("User with id {} not found", userId);
            return Optional.empty();
        }
    }
}
