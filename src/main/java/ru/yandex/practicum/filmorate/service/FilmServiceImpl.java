package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    @Override
    public void delete(Long filmId) {
        filmStorage.delete(filmId);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        if (film.getLikes().contains(userId)) {
            throw new ConditionsNotMetException("Пользователь с id = " + userId + " уже поставил лайк фильму с id = " + filmId);
        }
        film.getLikes().add(userId);
        log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        if (!film.getLikes().remove(userId)) {
            throw new ConditionsNotMetException("Пользователь с id = " + userId + " не ставил лайк фильму с id = " + filmId);
        }
        log.info("Пользователь с id = {} убрал лайк у фильма с id = {}", userId, filmId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        List<Film> allFilms = new ArrayList<>(filmStorage.findAll());

        allFilms.sort((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()));

        return allFilms.subList(0, Math.min(count, allFilms.size()));
    }
}