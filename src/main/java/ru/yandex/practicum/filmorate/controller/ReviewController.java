package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ReviewDto create(@Valid @RequestBody ReviewDto reviewDto) {
        log.info("Создание нового отзыва: {}", reviewDto);
        return reviewService.create(reviewDto);
    }

    @PutMapping
    public ReviewDto update(@Valid @RequestBody ReviewDto reviewDto) {
        log.info("Обновление отзыва с ID {}: {}", reviewDto.getReviewId(), reviewDto);
        return reviewService.update(reviewDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Удаление отзыва с ID: {}", id);
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public ReviewDto getById(@PathVariable Long id) {
        log.info("Получение отзыва с ID: {}", id);
        return reviewService.getById(id);
    }

    @GetMapping
    public List<ReviewDto> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") int count) {
        log.info("Получение отзывов для фильма ID: {}, количество: {}", filmId, count);
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Добавление лайка отзыву ID {} от пользователя {}", id, userId);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Добавление дизлайка отзыву ID {} от пользователя {}", id, userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удаление лайка отзыву ID {} от пользователя {}", id, userId);
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удаление дизлайка отзыву ID {} от пользователя {}", id, userId);
        reviewService.removeDislike(id, userId);
    }
}