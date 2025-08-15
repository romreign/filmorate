package com.example.filmorate.storage;

import com.example.filmorate.model.Film;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.filmorate.util.FilmValidation.validateFilmExists;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> filmMap = new HashMap<>();
    private long autoId = 1;

    private void incId() {
        ++autoId;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film getFilmById(long filmId) {
        validateFilmExists(filmMap, filmId);
        return filmMap.get(filmId);
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(autoId);
        filmMap.put(autoId, film);
        incId();
        return film;
    }

    @Override
    public Film updateFilm(long filmId, Film film) {
        validateFilmExists(filmMap, filmId);
        filmMap.put(filmId, film);
        return film;
    }
}
