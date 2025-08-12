package com.example.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private long id;
    @Email(message = "Mail does not pass validation")
    private String email;
    @NotBlank(message = "Login cannot contain spaces")
    private String login;
    private String name;
    @PastOrPresent(message = "The date cannot be in the future")
    private LocalDate birthday;
    private Set<Long> friends;
    private Set<Long> friendRequests;

    public User(long id, String email, String login, String name, LocalDate birthday, Set<Long> friends, Set<Long> friendRequests) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = (name == null || name.isEmpty()) ? login : name;
        this.birthday = birthday;
        this.friends = friends == null ? new HashSet<>() : friends;
        this.friendRequests = friendRequests == null ? new HashSet<>() : friendRequests;
    }
}
