package ru.yandex.practicum.filmorate.exception;

public class ReviewNotFoundException extends NotFoundException {
    public ReviewNotFoundException(Long id) {
        super("Отзыв с id " + id + " не найден");
    }
}