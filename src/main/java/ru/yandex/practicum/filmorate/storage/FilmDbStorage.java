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
import ru.yandex.practicum.filmorate.model.Director;
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
            SELECT f.*, m.name AS mpa_name, m.description AS mpa_description
            FROM films f LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            WHERE f.film_id = ?""";
    private static final String SQL_GET_ALL_FILM_IDS = "SELECT film_id FROM films ORDER BY film_id";

    // SQL-запросы для работы с жанрами
    private static final String SQL_INSERT_GENRES = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String SQL_DELETE_GENRES = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String SQL_GET_FILM_GENRES = """
            SELECT g.genre_id, g.name
            FROM film_genres fg
            JOIN genres g ON fg.genre_id = g.genre_id
            WHERE fg.film_id = ?
            ORDER BY g.genre_id""";

    // SQL-запросы для работы с лайками
    private static final String SQL_ADD_LIKE = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String SQL_REMOVE_LIKE = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String SQL_GET_LIKES = "SELECT user_id FROM film_likes WHERE film_id = ?";

    // SQL-запросы для поиска фильмов
    private static final String SQL_FIND_TOP_FILMS = """
            SELECT f.film_id
            FROM films f
            LEFT JOIN film_genres fg ON f.film_id = fg.film_id
            LEFT JOIN film_likes fl ON f.film_id = fl.film_id
            WHERE (? IS NULL OR fg.genre_id = ?)
            AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?)
            GROUP BY f.film_id
            ORDER BY COUNT(fl.user_id) DESC
            LIMIT ?""";

    private static final String SQL_FIND_COMMON_FILMS = """
            WITH common_films AS (
                SELECT fl1.film_id
                FROM film_likes fl1
                JOIN film_likes fl2 ON fl1.film_id = fl2.film_id
                WHERE fl1.user_id = ? AND fl2.user_id = ?
            )
            SELECT film_id FROM common_films""";

    private static final String SQL_FIND_FILMS_BY_DIRECTOR = """
            SELECT fd.film_id
            FROM film_directors fd
            JOIN films f ON fd.film_id = f.film_id
            WHERE fd.director_id = ?
            ORDER BY
                CASE WHEN ? = 'year' THEN f.release_date END ASC,
                (SELECT COUNT(*) FROM film_likes WHERE film_id = fd.film_id) DESC""";

    //Для работы с режиссёрами
    private static final String SQL_INSERT_DIRECTORS = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
    private static final String SQL_DELETE_DIRECTORS = "DELETE FROM film_directors WHERE film_id = ?";
    private static final String SQL_GET_FILM_DIRECTORS = """
            SELECT d.director_id, d.name
            FROM film_directors fd
            JOIN directors d ON fd.director_id = d.director_id
            WHERE fd.film_id = ?
            ORDER BY d.director_id""";

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

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            List<Director> uniqueDirectors = film.getDirectors().stream()
                    .distinct()
                    .toList();

            List<Object[]> batchArgs = uniqueDirectors.stream()
                    .map(director -> new Object[]{filmId, director.getId()})
                    .toList();

            jdbcTemplate.batchUpdate(SQL_INSERT_DIRECTORS, batchArgs);
            film.setDirectors(uniqueDirectors);
        }

        return film;
    }

    @Transactional
    public Film update(Film film) {
        log.debug("Updating film {} with directors: {}", film.getId(), film.getDirectors());

        jdbcTemplate.update(SQL_UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        updateFilmGenres(film);
        updateFilmDirectors(film);

        Film updated = findById(film.getId());
        log.debug("Film after update: {}", updated);
        return updated;
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
    public Film findById(Long filmId) {
        try {
            Film film = jdbcTemplate.queryForObject(SQL_FIND_FILM_BY_ID, filmRowMapper, filmId);
            log.debug("Found base film info for id {}: {}", filmId, film);

            List<Genre> genres = getFilmGenres(filmId);
            List<Director> directors = getFilmDirectors(filmId);
            Set<Long> likes = getLikes(filmId);

            log.debug("For film {} found: {} genres, {} directors, {} likes",
                    filmId, genres.size(), directors.size(), likes.size());

            film.setGenres(genres);
            film.setDirectors(directors);
            film.setLikes(likes);

            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Film> findAll() {
        // Получаем только ID всех фильмов
        List<Long> filmIds = jdbcTemplate.queryForList(SQL_GET_ALL_FILM_IDS, Long.class);

        // Для каждого ID получаем полную информацию через findById
        return filmIds.stream()
                .map(this::findById)
                .collect(Collectors.toList());
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
    public List<Film> findTopFilms(int count, Integer genreId, Integer year) {
        // Получаем ID топовых фильмов
        List<Long> filmIds = jdbcTemplate.queryForList(SQL_FIND_TOP_FILMS, Long.class,
                genreId, genreId,
                year, year,
                count);

        return filmIds.stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Film> findCommonFilms(Long userId, Long friendId) {
        // Получаем ID общих фильмов
        List<Long> filmIds = jdbcTemplate.queryForList(SQL_FIND_COMMON_FILMS, Long.class, userId, friendId);

        return filmIds.stream()
                .map(this::findById)
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size())) // Сортировка по убыванию лайков
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Film> findFilmsByDirector(Long directorId, String sortBy) {
        // Получаем ID фильмов режиссера с правильной сортировкой
        List<Long> filmIds = jdbcTemplate.queryForList(SQL_FIND_FILMS_BY_DIRECTOR, Long.class, directorId, sortBy);

        // Для каждого ID получаем полную информацию о фильме
        return filmIds.stream()
                .map(this::findById)
                .collect(Collectors.toList());
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

    private void updateFilmDirectors(Film film) {
        log.debug("Updating directors for film {}: {}", film.getId(), film.getDirectors());

        int deleted = jdbcTemplate.update(SQL_DELETE_DIRECTORS, film.getId());
        log.debug("Deleted {} directors for film {}", deleted, film.getId());

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            List<Object[]> batchArgs = film.getDirectors().stream()
                    .filter(Objects::nonNull)
                    .map(director -> new Object[]{film.getId(), director.getId()})
                    .collect(Collectors.toList());

            if (!batchArgs.isEmpty()) {
                int[] updateCounts = jdbcTemplate.batchUpdate(SQL_INSERT_DIRECTORS, batchArgs);
                log.debug("Added {} directors for film {}", updateCounts.length, film.getId());
            }
        }
    }

    private List<Director> getFilmDirectors(Long filmId) {
        return jdbcTemplate.query(SQL_GET_FILM_DIRECTORS,
                (rs, rowNum) -> new Director(rs.getLong("director_id"), rs.getString("name")),
                filmId);
    }
}