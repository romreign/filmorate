package com.example.filmorate.util;

import com.example.filmorate.exception.FilmNotFoundException;
import com.example.filmorate.exception.InvalidCountFilmException;
import com.example.filmorate.model.Film;

import java.util.Map;

public class FilmValidation {
    public static void validateFilmExists(Map<Long, Film> filmMap, long filmId) {
        if (!filmMap.containsKey(filmId))
            throw new FilmNotFoundException("Film not found: " + filmId);
    }

    public static void validateCountFilmExists(long count) {
        if (count < 1)
            throw new InvalidCountFilmException("The number of films cannot be less than 1");
    }
}
