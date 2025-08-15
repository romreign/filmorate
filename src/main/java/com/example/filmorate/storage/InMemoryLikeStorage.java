package com.example.filmorate.storage;

import com.example.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.filmorate.util.FilmValidation.validateFilmExists;

public class InMemoryLikeStorage implements LikeStorage{
    private final Map<Long, Film> filmMap;

    public InMemoryLikeStorage(Map<Long, Film> filmMap) {
        this.filmMap = filmMap;
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        return filmMap.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film addFilmLike(long filmId, long userId) {
        validateFilmExists(filmMap, filmId);
        filmMap.get(filmId).getLikes().add(userId);
        return filmMap.get(filmId);
    }

    @Override
    public Film deleteFilmLike(long filmId, long userId) {
        validateFilmExists(filmMap, filmId);
        filmMap.get(filmId).getLikes().remove(userId);
        return filmMap.get(filmId);
    }
}
