package com.example.filmorate.storage;

import com.example.filmorate.model.Film;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.filmorate.util.FilmValidation.validateFilmExists;

@Component
@AllArgsConstructor
public class InMemoryLikeStorage implements LikeStorage{
    private final InMemoryFilmStorage filmStorage;
    
    @Override
    public List<Film> getPopularFilm(int count) {
        return filmStorage.getFilmMap().values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film addFilmLike(long filmId, long userId) {
        validateFilmExists(filmStorage.getFilmMap(), filmId);
        filmStorage.getFilmMap().get(filmId).getLikes().add(userId);
        return filmStorage.getFilmMap().get(filmId);
    }

    @Override
    public Film deleteFilmLike(long filmId, long userId) {
        validateFilmExists(filmStorage.getFilmMap(), filmId);
        filmStorage.getFilmMap().get(filmId).getLikes().remove(userId);
        return filmStorage.getFilmMap().get(filmId);
    }
}
