package com.example.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
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
    @Builder.Default
    private Set<Long> friendIds = new HashSet<>();
    @Builder.Default
    private Set<Long> incomingRequestIds = new HashSet<>(); // Входящие заявки
    @Builder.Default
    private Set<Long> outgoingRequestIds = new HashSet<>();

    public User(long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = (name == null || name.isEmpty()) ? login : name;
        this.birthday = birthday;
    }

    public User(long id, String email, String login, String name, LocalDate birthday,
                Set<Long> friendIds, Set<Long> incomingRequestIds, Set<Long> outgoingRequestIds) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = (name == null || name.isEmpty()) ? login : name;
        this.birthday = birthday;
        this.friendIds = friendIds != null ? friendIds : new HashSet<>();
        this.incomingRequestIds = incomingRequestIds != null ? incomingRequestIds : new HashSet<>();
        this.outgoingRequestIds = outgoingRequestIds != null ? outgoingRequestIds : new HashSet<>();
    }
}
