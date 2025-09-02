package com.example.filmorate.storage.rowmapper;

import com.example.filmorate.model.Film;
import com.example.filmorate.model.Genre;
import com.example.filmorate.model.MPA;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        MPA mpa = MPA.builder()
                .id(rs.getLong("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();

        Set<Genre> genres = parseGenres(
                rs.getString("genre_ids"),
                rs.getString("genre_names")
        );

        Set<Long> likes = parseLikes(rs.getString("like_ids"));

        return  Film.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .genres(genres)
                .likes(likes)
                .likesCount(likes.size())
                .build();
    }

    private Set<Genre> parseGenres(String genreIdsStr, String genreNamesStr) {
        Set<Genre> genres = new HashSet<>();

        if (genreIdsStr != null && genreNamesStr != null) {
            String[] ids = genreIdsStr.split(",");
            String[] names = genreNamesStr.split(",");

            for (int i = 0; i < ids.length; i++)
                if (!ids[i].isEmpty() && i < names.length) {
                    Genre genre = Genre.builder()
                            .id(Long.parseLong(ids[i]))
                            .name(names[i])
                            .build();
                    genres.add(genre);
                }
        }

        return genres;
    }

    private Set<Long> parseLikes(String likeIdsStr) {
        Set<Long> likes = new HashSet<>();

        if (likeIdsStr != null) {
            String[] ids = likeIdsStr.split(",");
            for (String id : ids)
                if (!id.isEmpty())
                    likes.add(Long.parseLong(id));
        }

        return likes;
    }
}
