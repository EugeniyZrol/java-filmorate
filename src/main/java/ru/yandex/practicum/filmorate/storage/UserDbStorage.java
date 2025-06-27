package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    // SQL-запросы
    private static final String SQL_CREATE_USER =
            "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_USER =
            "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private static final String SQL_FIND_USER_BY_ID =
            "SELECT * FROM users WHERE user_id = ?";
    private static final String SQL_FIND_ALL_USERS =
            "SELECT * FROM users";
    private static final String SQL_DELETE_USER =
            "DELETE FROM users WHERE user_id = ?";
    private static final String SQL_CHECK_USER_EXISTS =
            "SELECT COUNT(*) FROM users WHERE user_id = ?";

    // Запросы для друзей
    private static final String SQL_ADD_FRIEND =
            "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
    private static final String SQL_REMOVE_FRIEND =
            "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String SQL_GET_FRIENDS =
            "SELECT friend_id FROM friendships WHERE user_id = ?";
    private static final String SQL_CHECK_FRIENDSHIP_EXISTS =
            "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String SQL_GET_COMMON_FRIENDS =
            "SELECT f1.friend_id " +
                    "FROM friendships f1 " +
                    "JOIN friendships f2 ON f1.friend_id = f2.friend_id " +
                    "WHERE f1.user_id = ? AND f2.user_id = ?";

    @Override
    @Transactional
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_CREATE_USER, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Создан пользователь с ID: {}", user.getId());
        return user;
    }

    @Override
    @Transactional
    public User update(User user) {
        if (!exists(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        jdbcTemplate.update(SQL_UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());

        log.info("Обновлен пользователь с ID: {}", user.getId());
        return user;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jdbcTemplate.update(SQL_REMOVE_FRIEND, id, id);
        int deleted = jdbcTemplate.update(SQL_DELETE_USER, id);
        if (deleted == 0) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        log.info("Удален пользователь с ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_USER_BY_ID, userRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL_USERS, userRowMapper);
    }

    @Override
    @Transactional
    public void addFriend(Long userId, Long friendId) {
        if (!friendshipExists(userId, friendId)) {
            jdbcTemplate.update(SQL_ADD_FRIEND, userId, friendId);
            log.info("Добавлена односторонняя дружба: {} -> {}", userId, friendId);
        } else {
            log.info("Дружба уже существует: {} -> {}", userId, friendId);
        }
    }

    @Override
    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        jdbcTemplate.update(SQL_REMOVE_FRIEND, userId, friendId);
        log.info("Попытка удаления дружбы: {} -> {}", userId, friendId);
    }

    @Override
    public Set<Long> getFriends(Long userId) {
        try {
            return new HashSet<>(jdbcTemplate.queryForList(
                    SQL_GET_FRIENDS,
                    Long.class,
                    userId
            ));
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptySet();
        }
    }

    @Override
    public Set<Long> getCommonFriends(Long userId1, Long userId2) {
        try {
            return new HashSet<>(jdbcTemplate.queryForList(
                    SQL_GET_COMMON_FRIENDS,
                    Long.class,
                    userId1,
                    userId2
            ));
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean exists(Long userId) {
        return jdbcTemplate.queryForObject(SQL_CHECK_USER_EXISTS, Integer.class, userId) > 0;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    SQL_FIND_USER_BY_ID,
                    userRowMapper,
                    id
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private boolean friendshipExists(Long userId, Long friendId) {
        return jdbcTemplate.queryForObject(
                SQL_CHECK_FRIENDSHIP_EXISTS,
                Integer.class,
                userId, friendId
        ) > 0;
    }
}