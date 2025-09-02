package com.example.filmorate.storage.impl;

import com.example.filmorate.exception.FilmNotFoundException;
import com.example.filmorate.model.Film;
import com.example.filmorate.model.Genre;
import com.example.filmorate.storage.FilmStorage;
import com.example.filmorate.storage.rowmapper.FilmRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    @Override
    public List<Film> getFilms() {
        String sql = """
            SELECT 
                f.*,
                m.name AS mpa_name,
                STRING_AGG(DISTINCT CAST(g.id AS TEXT), ',') AS genre_ids,
                STRING_AGG(DISTINCT g.name, ',') AS genre_names,
                STRING_AGG(DISTINCT CAST(l.user_id AS TEXT), ',') AS like_ids
            FROM films f
            JOIN mpa m ON f.mpa_id = m.id
            LEFT JOIN film_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            LEFT JOIN likes l ON f.id = l.film_id
            GROUP BY f.id, m.name
            ORDER BY f.id
            """;

        return jdbcTemplate.query(sql, filmRowMapper);
    }

    @Override
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO films (title, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, film.getTitle());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            preparedStatement.setLong(5, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);

        Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(filmId);

        saveFilmGenres(filmId, film.getGenres());

        log.info("Created film with ID: {}", filmId);
        return film;
    }

    @Override
    public Film updateFilm(long filmId, Film film) {

        String sql = """
                UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? 
                WHERE id = ?
                """;

        int updatedRows = jdbcTemplate.update(sql,
                film.getTitle(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                filmId);

        if (updatedRows == 0)
            throw new FilmNotFoundException("Film with id " + filmId + " not found");

        updateFilmGenres(filmId, film.getGenres());

        film.setId(filmId);
        log.info("Updated film with ID: {}", filmId);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {
        String sql = """
            SELECT 
                f.*,
                m.name AS mpa_name,
                STRING_AGG(DISTINCT CAST(g.id AS TEXT), ',') AS genre_ids,
                STRING_AGG(DISTINCT g.name, ',') AS genre_names,
                STRING_AGG(DISTINCT CAST(l.user_id AS TEXT), ',') AS like_ids
            FROM films f
            JOIN mpa m ON f.mpa_id = m.id
            LEFT JOIN film_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            LEFT JOIN likes l ON f.id = l.film_id
            WHERE f.id = ?
            GROUP BY f.id, m.name
            """;

        try {
            Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, filmId);
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private void saveFilmGenres(Long filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty())
            return;

        String sql = """
                INSERT INTO film_genres (film_id, genre_id) 
                VALUES (?, ?)
                """;
        List<Object[]> batchArgs = genres.stream()
                .map(genre -> new Object[]{filmId, genre.getId()})
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void updateFilmGenres(Long filmId, Set<Genre> newGenres) {
        String deleteSql = """
                           DELETE FROM film_genres
                           WHERE film_id = ?
                           """;
        jdbcTemplate.update(deleteSql, filmId);

        if (newGenres != null && !newGenres.isEmpty())
            saveFilmGenres(filmId, newGenres);
    }
}
