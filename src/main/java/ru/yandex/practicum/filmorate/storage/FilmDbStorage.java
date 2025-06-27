package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    // SQL-запросы для работы с фильмами
    private static final String SQL_CREATE_FILM = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String SQL_DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
    private static final String SQL_FIND_FILM_BY_ID = """
            SELECT f.*, m.name AS mpa_name, m.description AS mpa_description\s
            FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id\s
            WHERE f.film_id = ?""";

    // SQL-запросы для работы с жанрами
    private static final String SQL_INSERT_GENRES = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String SQL_DELETE_GENRES = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String SQL_GET_FILM_GENRES = """
            SELECT g.genre_id, g.name\s
            FROM film_genres fg\s
            JOIN genres g ON fg.genre_id = g.genre_id\s
            WHERE fg.film_id = ?
            ORDER BY g.genre_id""";
    private static final String SQL_FIND_ALL_FILMS_WITH_GENRES = """
            SELECT f.*, 
                   m.name AS mpa_name, 
                   m.description AS mpa_description,
                   g.genre_id AS genre_id,
                   g.name AS genre_name
            FROM films f 
            LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            LEFT JOIN film_genres fg ON f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            ORDER BY f.film_id, g.genre_id""";

    // SQL-запросы для работы с лайками
    private static final String SQL_ADD_LIKE =
            "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String SQL_REMOVE_LIKE =
            "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String SQL_GET_LIKES =
            "SELECT user_id FROM film_likes WHERE film_id = ?";
    private static final String SQL_GET_TOP_FILMS = """
            SELECT f.*, m.name AS mpa_name, m.description AS mpa_description
            FROM films f
            LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            LEFT JOIN film_likes fl ON f.film_id = fl.film_id
            GROUP BY f.film_id, m.mpa_id
            ORDER BY COUNT(fl.user_id) DESC
            LIMIT ?""";

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_CREATE_FILM, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());

            if (film.getMpa() != null) {
                stmt.setInt(5, film.getMpa().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            return stmt;
        }, keyHolder);

        long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(filmId);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Genre> uniqueGenres = film.getGenres().stream()
                    .distinct()
                    .toList();

            List<Object[]> batchArgs = uniqueGenres.stream()
                    .map(genre -> new Object[]{filmId, genre.getId()})
                    .toList();

            jdbcTemplate.batchUpdate(SQL_INSERT_GENRES, batchArgs);
            film.setGenres(uniqueGenres);
        }
        return film;
    }

    @Override
    @Transactional
    public Film update(Film film) {
        jdbcTemplate.update(SQL_UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        updateFilmGenres(film);
        log.info("Фильм с ID обновлен: {}", film.getId());
        return findById(film.getId());
    }

    @Override
    @Transactional
    public void delete(Long filmId) {
        int deleted = jdbcTemplate.update(SQL_DELETE_FILM, filmId);
        if (deleted == 0) {
            log.error("Удаление фильма с id не удалось: {}", filmId);
            throw new NotFoundException("Фильм с таким id не найден: " + filmId);
        }
        log.info("Фильм с ID удален: {}", filmId);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Film> findAll() {
        try {
            Map<Long, Film> filmMap = new LinkedHashMap<>();

            jdbcTemplate.query(SQL_FIND_ALL_FILMS_WITH_GENRES, rs -> {
                Long filmId = rs.getLong("film_id");
                Film film = filmMap.computeIfAbsent(filmId, id -> {
                    try {
                        return filmRowMapper.mapRow(rs, 0);
                    } catch (SQLException e) {
                        throw new DataAccessResourceFailureException("Не удалось преобразовать данные фильма", e);
                    }
                });

                int genreId = rs.getInt("genre_id");
                if (!rs.wasNull()) {
                    Genre genre = new Genre(genreId, rs.getString("genre_name"));
                    film.getGenres().add(genre);
                }
            });

            return new ArrayList<>(filmMap.values());
        } catch (DataAccessException e) {
            throw new DataRetrievalFailureException("Не удалось извлечь фильм", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Film findById(Long filmId) {
        try {
            Film film = jdbcTemplate.queryForObject(SQL_FIND_FILM_BY_ID, filmRowMapper, filmId);
            film.setLikes(getLikes(filmId));

            List<Genre> genres = getFilmGenres(filmId);
            film.setGenres(genres);

            return film;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Фильм с таким id не найден: {}", filmId);
            throw new NotFoundException("Фильм с таким id не найден: " + filmId);
        }
    }

    @Override
    @Transactional
    public void addLike(Long filmId, Long userId) {
        try {
            int updated = jdbcTemplate.update(
                    SQL_ADD_LIKE,
                    filmId,
                    userId
            );

            if (updated == 0) {
                throw new DuplicateKeyException("Повторный лайк");
            }
        } catch (DuplicateKeyException e) {
            log.debug("Лайк уже существует: film={}, user={}", filmId, userId);
            throw e;
        }
    }

    @Override
    @Transactional
    public void removeLike(Long filmId, Long userId) {
        jdbcTemplate.update(SQL_REMOVE_LIKE, filmId, userId);
        log.debug("Лайк для фильма: {}, удален пользователем: {}", filmId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getLikes(Long filmId) {
        return new HashSet<>(jdbcTemplate.queryForList(SQL_GET_LIKES, Long.class, filmId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Film> findTopFilms(int count) {
        return jdbcTemplate.query(SQL_GET_TOP_FILMS, filmRowMapper, count);
    }

    private void updateFilmGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            jdbcTemplate.update(SQL_DELETE_GENRES, film.getId());
            return;
        }

        jdbcTemplate.update(SQL_DELETE_GENRES, film.getId());

        List<Genre> uniqueGenres = film.getGenres().stream()
                .distinct()
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(SQL_INSERT_GENRES, uniqueGenres, uniqueGenres.size(),
                (ps, genre) -> {
                    ps.setLong(1, film.getId());
                    ps.setInt(2, genre.getId());
                });
    }

    private List<Genre> getFilmGenres(Long filmId) {
        return new ArrayList<>(jdbcTemplate.query(
                SQL_GET_FILM_GENRES,
                (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")),
                filmId
        ));
    }
}