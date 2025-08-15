package com.example.filmorate.storage;

import com.example.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();
    Film createFilm(Film film) ;
    Film updateFilm(long filmId, Film film);
    Film getFilmById(long filmId);
}
