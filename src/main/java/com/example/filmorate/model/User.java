package com.example.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;
    @Email(message = "Почта не проходит валидацию")
    private String email;
    @NotBlank(message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата не может быть в будущем.")
    private LocalDate birthday;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = (name == null || name.isEmpty()) ? login : name;
        this.birthday = birthday;
    }
}
