package com.example.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private static final short MAX_LENGTH_DESCRIPTION = 200;
    private int id;
    @Positive(message = "Duration must be positive")
    private long duration;
    private LocalDate releaseDate;
    @NotBlank(message = "The name cannot be empty")
    private String title;
    @Size(max = MAX_LENGTH_DESCRIPTION, message = "The description length cannot be longer " + MAX_LENGTH_DESCRIPTION)
    private String description;
    private Genre genre;
    private MPA mpa;
    private Set<Long> likes;
}
