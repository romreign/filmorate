package com.example.filmorate.service;

import com.example.filmorate.exception.UserNotFoundException;
import com.example.filmorate.model.Film;
import com.example.filmorate.storage.FilmStorage;
import com.example.filmorate.storage.LikeStorage;
import com.example.filmorate.storage.UserStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.filmorate.util.FilmValidation.validateCountFilmExists;
import static com.example.filmorate.util.IdValidation.validateIdExists;

@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Optional<Film> getFilmById(long filmId) {
        validateIdExists(filmId);
        return filmStorage.getFilmById(filmId);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(long filmId, Film film) {
        validateIdExists(filmId);
        return filmStorage.updateFilm(filmId, film);
    }

    public List<Film> getPopularFilm(int count) {
        validateCountFilmExists(count);
        return likeStorage.getPopularFilm(count);
    }

    public Film addFilmLike(long filmId, long userId) {
        validateIdExists(filmId);
        validateIdExists(userId);
        userStorage.getUserById(userId).orElseThrow(() ->
                new UserNotFoundException("User not found"));
        return likeStorage.addFilmLike(filmId, userId);
    }

    public Film deleteFilmLike(long filmId, long userId) {
        validateIdExists(filmId);
        validateIdExists(userId);
        userStorage.getUserById(userId).orElseThrow(() ->
                new UserNotFoundException("User not found"));
        return likeStorage.deleteFilmLike(filmId, userId);
    }
}
