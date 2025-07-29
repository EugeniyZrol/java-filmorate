package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewMapper reviewMapper;
    private final FeedStorage feedStorage;

    @Override
    public ReviewDto create(ReviewDto reviewDto) {
        validateUserAndFilm(reviewDto.getUserId(), reviewDto.getFilmId());
        Review review = reviewMapper.toEntity(reviewDto);
        Review createdReview = reviewStorage.create(review);

        UserFeed feed = new UserFeed();
        feed.setUserId(reviewDto.getUserId());
        feed.setEntityId(createdReview.getReviewId());
        feed.setEventType(UserFeed.EventType.REVIEW);
        feed.setOperation(UserFeed.Operation.ADD);
        feed.setTimestamp(System.currentTimeMillis());
        feedStorage.create(feed);

        return reviewMapper.toDto(createdReview);
    }

    @Override
    public ReviewDto update(ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);
        if (!reviewStorage.exists(review.getReviewId())) {
            throw new ReviewNotFoundException(review.getReviewId());
        }

        Review updatedReview = reviewStorage.update(review);

        UserFeed feed = new UserFeed();
        feed.setUserId(reviewDto.getUserId());
        feed.setEntityId(updatedReview.getReviewId());
        feed.setEventType(UserFeed.EventType.REVIEW);
        feed.setOperation(UserFeed.Operation.UPDATE);
        feed.setTimestamp(System.currentTimeMillis());
        feedStorage.create(feed);

        return reviewMapper.toDto(updatedReview);
    }

    @Override
    public void delete(Long id) {
        if (!reviewStorage.exists(id)) {
            throw new ReviewNotFoundException(id);
        }
        Review review = reviewStorage.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));

        UserFeed feed = new UserFeed();
        feed.setUserId(review.getUserId());
        feed.setEntityId(id);
        feed.setEventType(UserFeed.EventType.REVIEW);
        feed.setOperation(UserFeed.Operation.REMOVE);
        feed.setTimestamp(System.currentTimeMillis());
        feedStorage.create(feed);

        reviewStorage.delete(id);
    }

    @Override
    public ReviewDto getById(Long id) {
        return reviewStorage.findById(id)
                .map(reviewMapper::toDto)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    @Override
    public List<ReviewDto> getReviews(Long filmId, int count) {
        if (filmId != null) {
            if (!filmStorage.exists(filmId)) {
                throw new NotFoundException("Фильм с id " + filmId + " не найден");
            }
            return reviewStorage.findAllByFilmId(filmId, count).stream()
                    .map(reviewMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return reviewStorage.findAllByFilmId(null, count).stream()
                    .map(reviewMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.addLike(reviewId, userId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.addDislike(reviewId, userId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.removeLike(reviewId, userId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.removeDislike(reviewId, userId);
    }

    private void validateUserAndFilm(Long userId, Long filmId) {
        if (userId <= 0) {
            throw new NotFoundException("Invalid user ID");
        }
        if (filmId <= 0) {
            throw new NotFoundException("Invalid film ID");
        }
    }

    private void validateReviewAndUser(Long reviewId, Long userId) {
        if (!reviewStorage.exists(reviewId)) {
            throw new ReviewNotFoundException(reviewId);
        }
        if (!userStorage.exists(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}