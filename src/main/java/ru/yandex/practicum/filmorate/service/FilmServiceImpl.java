package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

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
    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findAll().stream()
                .filter(f -> f.getId().equals(filmId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));

        if (userStorage.findAll().stream().noneMatch(u -> u.getId().equals(userId))) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (film.getLikes().contains(userId)) {
            throw new ConditionsNotMetException("Пользователь с id = " + userId + " уже поставил лайк фильму с id = " + filmId);
        }
        film.getLikes().add(userId);
        log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findAll().stream()
                .filter(f -> f.getId().equals(filmId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));

        if (userStorage.findAll().stream().noneMatch(u -> u.getId().equals(userId))) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        if (!film.getLikes().remove(userId)) {
            throw new ConditionsNotMetException("Пользователь с id = " + userId + " не ставил лайк фильму с id = " + filmId);
        }
        log.info("Пользователь с id = {} убрал лайк у фильма с id = {}", userId, filmId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}