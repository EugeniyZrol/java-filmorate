package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Вызов метода findAll для получения всех фильмов.");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Запрос на создание фильма: {}", film);

        if (film.getName().isBlank()) {
            log.error("Ошибка: Название не может быть пустым");
            throw new ConditionsNotMetException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Ошибка: Превышена Максимальная длина описания");
            throw new IllegalArgumentException("Превышена Максимальная длина описания");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Ошибка: Дата релиза не может быть раньше 28 декабря 1895 года.");
            throw new IllegalArgumentException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() < 0) {
            log.error("Ошибка: Продолжительность фильма должна быть положительным числом.");
            throw new IllegalArgumentException("Продолжительность фильма должна быть положительным числом.");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно создан: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {

        if (newFilm.getId() == null) {
            log.error("Id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.error("Фильм с id = {} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        Film oldFilm = films.get(newFilm.getId());

        if (!newFilm.getDescription().isBlank()) {
            oldFilm.setDescription(newFilm.getDescription());
            log.info("Описание фильма с id = {} обновлено", newFilm.getId());
        }

        if (!newFilm.getName().isBlank()) {
            oldFilm.setName(newFilm.getName());
            log.info("Название фильма с id = {} обновлено", newFilm.getId());
        }

        if (newFilm.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Дата релиза не может быть раньше 28 декабря 1895 года.");
            throw new IllegalArgumentException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        log.info("Дата релиза фильма с id = {} обновлена", newFilm.getId());

        if (newFilm.getDuration() < 0) {
            log.error("Продолжительность фильма должна быть положительным числом.");
            throw new IllegalArgumentException("Продолжительность фильма должна быть положительным числом.");
        } else {
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Продолжительность фильма с id = {} обновлена", newFilm.getId());
        }

        return oldFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}