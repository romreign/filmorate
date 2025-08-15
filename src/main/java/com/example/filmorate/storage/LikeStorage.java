package com.example.filmorate.storage;

import com.example.filmorate.model.Film;

import java.util.List;

public interface LikeStorage {
    List<Film> getPopularFilm(int count);
    Film addFilmLike(long filmId, long userId);
    Film deleteFilmLike(long filmId, long userId);
}
