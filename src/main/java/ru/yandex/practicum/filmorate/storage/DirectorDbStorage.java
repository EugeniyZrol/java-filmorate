package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    // SQL queries as constants
    private static final String SQL_FIND_ALL = "SELECT * FROM directors";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM directors WHERE director_id = ?";
    private static final String SQL_INSERT = "INSERT INTO directors (name) VALUES (?)";
    private static final String SQL_UPDATE = "UPDATE directors SET name = ? WHERE director_id = ?";
    private static final String SQL_DELETE = "DELETE FROM directors WHERE director_id = ?";

    @Override
    public List<Director> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL, (rs, rowNum) -> new Director(
                rs.getLong("director_id"),
                rs.getString("name")
        ));
    }

    @Override
    public Optional<Director> findById(Long id) {
        return jdbcTemplate.query(SQL_FIND_BY_ID, (rs, rowNum) -> new Director(
                rs.getLong("director_id"),
                rs.getString("name")
        ), id).stream().findFirst();
    }

    @Override
    public Director create(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return director;
    }

    @Override
    public Optional<Director> update(Director director) {
        int updated = jdbcTemplate.update(SQL_UPDATE, director.getName(), director.getId());
        return updated == 0 ? Optional.empty() : Optional.of(director);
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(SQL_DELETE, id);
    }
}