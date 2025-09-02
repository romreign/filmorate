package com.example.filmorate.storage.rowmapper;

import com.example.filmorate.model.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        LocalDate birthday = Optional.ofNullable(rs.getDate("birthday"))
                .map(Date::toLocalDate)
                .orElse(null);

        User user = User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(birthday)
                .build();

        parseFriendsAndRequests(rs, user);

        return user;
    }

    private void parseFriendsAndRequests(ResultSet rs, User user) throws SQLException {
        String friendIdsStr = rs.getString("friend_ids");
        if (friendIdsStr != null) {
            Set<Long> friendIds = Arrays.stream(friendIdsStr.split(","))
                    .filter(id -> !id.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
            user.setFriendIds(friendIds);
        }

        String incomingRequestIdsStr = rs.getString("incoming_request_ids");
        if (incomingRequestIdsStr != null) {
            Set<Long> incomingRequestIds = Arrays.stream(incomingRequestIdsStr.split(","))
                    .filter(id -> !id.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
            user.setIncomingRequestIds(incomingRequestIds);
        }

        String outgoingRequestIdsStr = rs.getString("outgoing_request_ids");
        if (outgoingRequestIdsStr != null) {
            Set<Long> outgoingRequestIds = Arrays.stream(outgoingRequestIdsStr.split(","))
                    .filter(id -> !id.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
            user.setOutgoingRequestIds(outgoingRequestIds);
        }
    }
}
