package com.example.filmorate.storage;

import com.example.filmorate.exception.FilmNotFoundException;
import com.example.filmorate.model.Film;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmMap = new HashMap<>();
    private int autoId = 1;

    private void incId() {
        ++autoId;
    }

    public List<Film> getFilms() {
        return new ArrayList<>(filmMap.values());
    }

    public Film getFilmById(int filmId) {
        if (!filmMap.containsKey(filmId))
            throw new FilmNotFoundException("Не найден фильм с id " + filmId);
        return filmMap.get(filmId);
    }

    public Film createFilm(Film film) {
        film.setId(autoId);
        filmMap.put(autoId, film);
        incId();
        return film;
    }

    public Film updateFilm(int filmId, Film film) {
        if (!filmMap.containsKey(filmId))
            throw new FilmNotFoundException("Не найден фильм с id " + filmId);
        filmMap.put(filmId, film);
        return film;
    }
}
