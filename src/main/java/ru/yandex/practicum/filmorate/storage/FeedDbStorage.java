package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.dal.UserFeedRowMapper;

import java.sql.PreparedStatement;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserFeedRowMapper userFeedRowMapper;

    private static final String SQL_CREATE_FEED = "INSERT INTO user_feeds (user_id, entity_id, event_type, operation, timestamp) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_FIND_BY_USER_ID = "SELECT * FROM user_feeds WHERE user_id = ? ORDER BY timestamp ASC";

    @Override
    public UserFeed create(UserFeed feed) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_CREATE_FEED, new String[]{"event_id"});
            stmt.setLong(1, feed.getUserId());
            stmt.setLong(2, feed.getEntityId());
            stmt.setString(3, feed.getEventType().name());
            stmt.setString(4, feed.getOperation().name());
            stmt.setLong(5, feed.getTimestamp());
            return stmt;
        }, keyHolder);

        feed.setEventId(keyHolder.getKey().longValue());
        return feed;
    }

    @Override
    public List<UserFeed> findByUserId(Long userId) {
        return jdbcTemplate.query(SQL_FIND_BY_USER_ID, userFeedRowMapper, userId);
    }
}