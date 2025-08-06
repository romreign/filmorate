package com.example.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private static final short MAX_LENGTH_DESCRIPTION = 200;
    private int id;
    @Positive(message = "Продолжительность должна быть положительной")
    private long duration;
    //ReleaseDate(min = "1895-12-28", message = "Дата релиза не может быть ранее 28 декабря 1895 года.")
    private LocalDate releaseDate;
    @NotBlank(message = "Название не может быть пустым")
    private String title;
    @Size(max = MAX_LENGTH_DESCRIPTION, message = "Длина описания не может быть больше " + MAX_LENGTH_DESCRIPTION)
    private String description;
}
