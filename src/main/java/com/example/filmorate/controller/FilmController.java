package com.example.filmorate.controller;

import com.example.filmorate.exception.FilmNotFoundException;
import com.example.filmorate.model.Film;
import com.example.filmorate.service.FilmService;
import com.example.filmorate.storage.FilmStorage;
import com.example.filmorate.storage.InMemoryFilmStorage;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@Slf4j
@RequestMapping("/api/films")
public class FilmController {
    private final FilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получение списка фильмов.");
        return inMemoryFilmStorage.getFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable int filmId) {
        log.info("Получение фильма с id " + filmId);
        return inMemoryFilmStorage.getFilmById(filmId);
    }

    @GetMapping("/popular?count={count}")
    public List<Film> getPopularFilm(@RequestParam int count) {
        log.info("Получение списка из первых фильмов по количеству лайков. Количество фильмов " + count);
        return filmService.getPopularFilm(count);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Создание фильма {}", film.getTitle());
        return inMemoryFilmStorage.createFilm(film);
    }

    @PutMapping("/{filmId}")
    public Film updateFilm(@PathVariable int filmId, @Valid @RequestBody Film film) {
        log.info("Обновление фильма с id {}", filmId);
        return inMemoryFilmStorage.updateFilm(filmId, film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addFilmLike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("Пользователь с id " + userId + " ставит лайк фильму с id " + filmId);
        return filmService.addFilmLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteFilmLike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("Пользователь с id " + userId + " удаляет лайк у фильма с id " + filmId);
        return filmService.deleteFilmLike(filmId, userId);
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
