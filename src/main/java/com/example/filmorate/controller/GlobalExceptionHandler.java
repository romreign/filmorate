package com.example.filmorate.controller;

import com.example.filmorate.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleFilmNotFound(final FilmNotFoundException e) {
        return Map.of(
                "error", "Film not found",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(final UserNotFoundException e) {
        return Map.of(
                "error", "User not found",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailAlreadyExists(final EmailAlreadyExistsException e) {
        return Map.of(
                "error", "Error with email parameter",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleFriendRequestNotFound(final FriendRequestNotFoundException e) {
        return Map.of(
                "error", "Error with user and friend id",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleFriendshipIdConflict(final FriendshipIdConflictException e) {
        return Map.of(
                "error", "Friendship ID conflict",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidId(final InvalidIdException e) {
        return Map.of(
                "error", "Invalid ID provided",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidCountFilm(final InvalidCountFilmException e) {
        return Map.of(
                "error", "Invalid film count provided",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleLikeNotFound(final LikeNotFoundException e) {
        return Map.of(
                "error", "Like not found",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleLikeAlreadyExists(final LikeAlreadyExistsException e) {
        return Map.of(
                "error", "Like already exists",
                "errorMessage", e.getMessage()
        );
    }
}