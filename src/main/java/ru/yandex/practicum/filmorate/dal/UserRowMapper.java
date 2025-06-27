package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;


@Slf4j
@Component
public class UserRowMapper implements RowMapper<User> {
    private final JdbcTemplate jdbcTemplate;

    public UserRowMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        try {
            user.setId(rs.getLong("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            String name = rs.getString("name");
            user.setName(name == null || name.isBlank() ? user.getLogin() : name);
            Date birthday = rs.getDate("birthday");
            user.setBirthday(birthday != null ? birthday.toLocalDate() : null);

            Set<Long> friends = new HashSet<>(jdbcTemplate.queryForList(
                    "SELECT friend_id FROM friendships WHERE user_id = ?",
                    Long.class,
                    user.getId()
            ));

            user.setFriends(friends);
            return user;
        } catch (SQLException e) {
            log.error("Ошибка преобразования данных пользователя", e);
            throw e;
        }
    }
}