package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    void delete(Long filmId);

    Collection<Film> findAll();

    Film findById(Long filmId);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Set<Long> getLikes(Long filmId);

    List<Film> findTopFilms(int count);

    List<Film> findCommonFilms(Long userId, Long friendId);
}