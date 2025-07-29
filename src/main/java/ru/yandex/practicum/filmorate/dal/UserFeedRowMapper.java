package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserFeed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserFeedRowMapper implements RowMapper<UserFeed> {
    @Override
    public UserFeed mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserFeed(
                rs.getLong("event_id"),
                rs.getLong("user_id"),
                UserFeed.EventType.valueOf(rs.getString("event_type")),
                UserFeed.Operation.valueOf(rs.getString("operation")),
                rs.getLong("entity_id"),
                rs.getLong("timestamp")
        );
    }
}