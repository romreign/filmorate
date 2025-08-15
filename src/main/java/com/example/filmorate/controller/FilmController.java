package com.example.filmorate.controller;

import com.example.filmorate.exception.FilmNotFoundException;
import com.example.filmorate.exception.InvalidCountFilmException;
import com.example.filmorate.exception.InvalidIdException;
import com.example.filmorate.model.Film;
import com.example.filmorate.service.FilmService;
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
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Getting a list of movies");
        return filmService.getFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable long filmId) {
        log.info("Getting movie with id {}", filmId);
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/popular?count={count}")
    public List<Film> getPopularFilm(@RequestParam(defaultValue = "10") int count) {
        log.info("Getting a list of the first movies by the number of likes. Number of movies {}", count);
        return filmService.getPopularFilm(count);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Making a movie {}", film.getTitle());
        return filmService.createFilm(film);
    }

    @PutMapping("/{filmId}")
    public Film updateFilm(@PathVariable long filmId, @Valid @RequestBody Film film) {
        log.info("Movie update with id {}", filmId);
        return filmService.updateFilm(filmId, film);
    }

    @PatchMapping("/{filmId}/like/{userId}")
    public Film addFilmLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("User with id {} likes movie with id {}", userId, filmId);
        return filmService.addFilmLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteFilmLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("User with id {} removes like from movie with id {}", userId, filmId);
        return filmService.deleteFilmLike(filmId, userId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleFilmNotFound(final FilmNotFoundException e) {
        return Map.of(
                "error", "Film not found",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidIdException(final InvalidIdException e) {
        return Map.of(
                "error", "Invalid ID provided",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidCountFilmException(final InvalidCountFilmException e) {
        return Map.of(
                "error", "Invalid film count provided",
                "errorMessage", e.getMessage()
        );
    }
}
