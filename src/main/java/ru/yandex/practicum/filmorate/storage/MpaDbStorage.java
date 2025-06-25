package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_FIND_ALL_MPA =
            "SELECT mpa_id AS id, name FROM mpa_ratings ORDER BY mpa_id";
    private static final String SQL_FIND_MPA_BY_ID =
            "SELECT mpa_id AS id, name FROM mpa_ratings WHERE mpa_id = ?";
    private static final String SQL_CHECK_MPA_EXISTS =
            "SELECT EXISTS(SELECT 1 FROM mpa_ratings WHERE mpa_id = ?)";
    private static final String SQL_GET_FILMS_COUNT_BY_MPA =
            "SELECT COUNT(*) FROM films WHERE mpa_id = ?";

    @Override
    @Transactional(readOnly = true)
    public List<MpaRating> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL_MPA, this::mapRowToMpa);
    }

    @Override
    @Transactional(readOnly = true)
    public MpaRating findById(Integer mpaId) {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_MPA_BY_ID, this::mapRowToMpa, mpaId);
        } catch (EmptyResultDataAccessException e) {
            log.warn("MPA rating not found with ID: {}", mpaId);
            throw new NotFoundException("MPA rating not found with id: " + mpaId);
        }
    }

    private MpaRating mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpa = new MpaRating();
        mpa.setId(rs.getInt("id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }
}