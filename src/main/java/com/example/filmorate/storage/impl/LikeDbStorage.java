package com.example.filmorate.storage.impl;

import com.example.filmorate.exception.FilmNotFoundException;
import com.example.filmorate.exception.LikeAlreadyExistsException;
import com.example.filmorate.exception.LikeNotFoundException;
import com.example.filmorate.model.Film;
import com.example.filmorate.storage.LikeStorage;
import com.example.filmorate.storage.rowmapper.FilmRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    @Override
    public List<Film> getPopularFilm(int count) {
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
            ORDER BY COUNT(l.user_id) DESC
            LIMIT ?
            """;

        return jdbcTemplate.query(sql, filmRowMapper, count);
    }

    @Override
    public Film addFilmLike(long filmId, long userId) {
        String sql = """
                INSERT INTO likes (film_id, user_id) 
                VALUES (?, ?)
                """;

        try {
            jdbcTemplate.update(sql, filmId, userId);
            log.info("User {} liked film {}", userId, filmId);
            return getFilmWithLikes(filmId);
        } catch (DuplicateKeyException e) {
            log.warn("User {} already liked film {}", userId, filmId);
            throw new LikeAlreadyExistsException("User " + userId + " already liked film " + filmId);
        }
    }

    @Override
    public Film deleteFilmLike(long filmId, long userId) {
        String sql = """
                DELETE FROM likes 
                WHERE film_id = ? AND user_id = ?""";

        int deletedRows = jdbcTemplate.update(sql, filmId, userId);

        if (deletedRows > 0) {
            log.info("User {} removed like from film {}", userId, filmId);
            return getFilmWithLikes(filmId);
        } else {
            log.warn("Like not found for user {} and film {}", userId, filmId);
            throw new LikeNotFoundException("User " + userId + " has not liked film " + filmId);
        }
    }

    private Film getFilmWithLikes(long filmId) {
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
            return jdbcTemplate.queryForObject(sql, filmRowMapper, filmId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Film with id {} not found after like operation", filmId);
            throw new FilmNotFoundException("Film with id " + filmId + " not found");
        }
    }

}
