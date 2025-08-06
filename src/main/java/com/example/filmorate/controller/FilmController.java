package com.example.filmorate.controller;

import com.example.filmorate.exception.FilmNotFoundException;
import com.example.filmorate.model.Film;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@Slf4j
@RequestMapping("/api/films")
public class FilmController {
    private final Map<Integer, Film> filmMap = new HashMap<>();
    private int autoId = 1;

    private void incId() {
        ++autoId;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получение списка фильмов.");
        return new ArrayList<>(filmMap.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Создание фильма {}", film.getTitle());
        film.setId(autoId);
        filmMap.put(autoId, film);
        incId();
        return film;
    }

    @PutMapping("/{filmId}")
    public Film updateFilm(@PathVariable int filmId, @Valid @RequestBody Film film) {
        if (!filmMap.containsKey(filmId))
            throw new FilmNotFoundException("Не найден фильм с id " + filmId);
        log.info("Обновление фильма с id {}", filmId);
        filmMap.put(filmId, film);
        return film;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleFilmNotFound(final FilmNotFoundException e) {
        return Map.of(
                "error", "Ошибка с параметром id",
                "errorMessage", e.getMessage()
        );
    }
}
