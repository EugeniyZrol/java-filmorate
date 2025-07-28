package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.ReviewRowMapper;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;

    // SQL-запросы для работы с отзывами
    private static final String SQL_UPDATE_REVIEW = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
    private static final String SQL_DELETE_REVIEW = "DELETE FROM reviews WHERE review_id = ?";
    private static final String SQL_FIND_REVIEW_BY_ID = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String SQL_FIND_ALL_BY_FILM_ID = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
    private static final String SQL_EXISTS_REVIEW = "SELECT COUNT(*) FROM reviews WHERE review_id = ?";

    // SQL-запросы для работы с лайками/дизлайками
    private static final String SQL_REMOVE_LIKE = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = true";
    private static final String SQL_REMOVE_DISLIKE = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = false";
    private static final String SQL_REMOVE_REACTION = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String SQL_ADD_REACTION = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";
    private static final String SQL_UPDATE_USEFUL = """
            UPDATE reviews r
            SET useful = (
                SELECT COALESCE(SUM(CASE WHEN is_like THEN 1 ELSE -1 END), 0)
                FROM review_likes
                WHERE review_id = r.review_id
            )
            WHERE review_id = ?""";

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> values = new HashMap<>();
        values.put("content", review.getContent());
        values.put("is_positive", review.getIsPositive());
        values.put("user_id", review.getUserId());
        values.put("film_id", review.getFilmId());
        values.put("useful", review.getUseful());

        Long id = simpleJdbcInsert.executeAndReturnKey(values).longValue();
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review update(Review review) {
        jdbcTemplate.update(SQL_UPDATE_REVIEW, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findById(review.getReviewId()).orElseThrow();
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(SQL_DELETE_REVIEW, id);
    }

    @Override
    public Optional<Review> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SQL_FIND_REVIEW_BY_ID, reviewRowMapper, id));
        } catch (Exception e) {
            throw new ReviewNotFoundException(id);
        }
    }

    @Override
    public List<Review> findAllByFilmId(Long filmId, int count) {
        return jdbcTemplate.query(SQL_FIND_ALL_BY_FILM_ID, reviewRowMapper, filmId, count);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        updateReaction(reviewId, userId, true);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        updateReaction(reviewId, userId, false);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        jdbcTemplate.update(SQL_REMOVE_LIKE, reviewId, userId);
        updateUseful(reviewId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        jdbcTemplate.update(SQL_REMOVE_DISLIKE, reviewId, userId);
        updateUseful(reviewId);
    }

    @Override
    public boolean exists(Long id) {
        Integer count = jdbcTemplate.queryForObject(SQL_EXISTS_REVIEW, Integer.class, id);
        return count > 0;
    }

    private void updateUseful(Long reviewId) {
        jdbcTemplate.update(SQL_UPDATE_USEFUL, reviewId);
    }

    private void updateReaction(Long reviewId, Long userId, boolean isLike) {
        jdbcTemplate.update(SQL_REMOVE_REACTION, reviewId, userId);
        jdbcTemplate.update(SQL_ADD_REACTION, reviewId, userId, isLike);
        updateUseful(reviewId);
    }
}