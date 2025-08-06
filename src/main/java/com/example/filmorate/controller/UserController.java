package com.example.filmorate.controller;

import com.example.filmorate.exception.EmailAlreadyExistsException;
import com.example.filmorate.exception.UserNotFoundException;
import com.example.filmorate.model.User;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Validated
@RestController
@Slf4j
@RequestMapping("/api/users")
public class UserController {
    private final Map<Integer, User> userMap = new HashMap<>();
    private final Map<String, Integer> emailToIdMap = new HashMap<>();
    private int autoId = 1;

    private void incId() {
        ++autoId;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получение списка пользователей.");
        return new ArrayList<>(userMap.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание пользователя с почтой {}", user.getEmail());
        user.setId(autoId);
        userMap.put(autoId, user);
        emailToIdMap.put(user.getEmail(), autoId);
        incId();
        return user;
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable int userId, @Valid @RequestBody User newUser) {
        if (!userMap.containsKey(userId))
            throw new UserNotFoundException("Не найден пользователь с id" + userId);
        if (emailToIdMap.containsKey(newUser.getEmail()))
            throw new EmailAlreadyExistsException("Пользователь с email " + newUser.getEmail() + " уже существует");

        if (!userMap.get(userId).getEmail().equals(newUser.getEmail()))
            emailToIdMap.put(newUser.getEmail(), userId);
        log.info("Обновление пользователя с id {}", userId);
        userMap.put(userId, newUser);
        return newUser;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(final UserNotFoundException e) {
        return Map.of(
                "error", "Ошибка с параметром id",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailAlreadyExists(final EmailAlreadyExistsException e) {
        return Map.of(
                "error", "Ошибка с параметром email",
                "errorMessage", e.getMessage()
        );
    }
}
