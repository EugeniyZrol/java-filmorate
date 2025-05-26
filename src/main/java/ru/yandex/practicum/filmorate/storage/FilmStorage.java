package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {

    Film update(Film film);

    Film create(Film film);

    void delete(Long filmId);

    Collection<Film> findAll();

    Film findById(Long filmId);
}