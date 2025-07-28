package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto create(ReviewDto reviewDto);

    ReviewDto update(ReviewDto reviewDto);

    void delete(Long id);

    ReviewDto getById(Long id);

    List<ReviewDto> getReviews(Long filmId, int count);

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void removeLike(Long reviewId, Long userId);

    void removeDislike(Long reviewId, Long userId);
}