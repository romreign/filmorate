package com.example.filmorate.controller;

import com.example.filmorate.exception.*;
import com.example.filmorate.model.User;
import com.example.filmorate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Validated
@RestController
@Slf4j
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        log.info("Getting a list of users");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public Optional<User> getUserById(@PathVariable long userId) {
        log.info("Getting user with id {}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable long userId) {
        log.info("Getting a list of friends from a user with id {}", userId);
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long userId, @PathVariable long otherId) {
        log.info("Getting a common list of friends from users with id {} and {}", userId, otherId);
        return userService.getCommonFriends(userId, otherId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Create a user with mail {}", user.getEmail());
        return userService.createUser(user);
    }

    @PostMapping("/{userId}/friends/{friendId}")
    public User sendFriendRequest(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Send requset a friend with id {} by user with id {}", friendId, userId);
        return userService.sendFriendRequest(userId, friendId);
    }

    @PatchMapping("/{userId}/friends/{friendId}/confirm")
    public User confirmFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Confirmation of friendship {} with {}", userId, friendId);
        return userService.confirmFriend(userId, friendId);
    }

    @PatchMapping("/{userId}/friends/{requesterId}/reject")
    public User rejectFriendRequest(@PathVariable long userId, @PathVariable long requesterId) {
        log.info("Refusal of friendship {} with {} id", userId, requesterId);
        return userService.rejectFriendRequest(userId, requesterId);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable long userId, @Valid @RequestBody User user) {
        log.info("Update user with id {}", userId);
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Removing friend with id {} by user with id {}", friendId, userId);
        return userService.deleteFriend(userId, friendId);
    }
}
