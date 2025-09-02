package com.example.filmorate.storage;

import com.example.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getFilms();
    Film createFilm(Film film) ;
    Film updateFilm(long filmId, Film film);
    Optional<Film> getFilmById(long filmId);
}
