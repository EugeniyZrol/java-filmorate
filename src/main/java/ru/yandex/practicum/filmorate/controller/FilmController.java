package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmResponseDto> findAll() {
        log.info("Вызов метода findAll для получения всех фильмов.");
        return filmService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmResponseDto create(@Validated(OnCreate.class) @RequestBody FilmRequestDto filmDto) {
        log.info("Запрос на создание фильма: {}", filmDto);
        FilmResponseDto createdFilm = filmService.create(filmDto);
        log.info("Фильм успешно создан: {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public FilmResponseDto update(@Validated(OnUpdate.class) @RequestBody FilmRequestDto filmDto) {
        log.info("Запрос на обновление фильма: {}", filmDto);
        return filmService.update(filmDto);
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long filmId) {
        log.info("Запрос на удаление фильма с id: {}", filmId);
        filmService.delete(filmId);
        log.info("Фильм с id {} успешно удален", filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
        log.info("Пользователь с id = {} поставил лайк фильму с id {}", userId, filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.removeLike(filmId, userId);
        log.info("Пользователь с id = {} убрал лайк у фильма с id = {}", userId, filmId);
    }

    @GetMapping("/popular")
    public List<FilmResponseDto> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрос топ-{} фильмов", count);
        return filmService.getTopFilms(count);
    }

    @GetMapping("/{id}")
    public FilmResponseDto getFilmById(@PathVariable Long id) {
        log.info("Запрос фильма по id: {}", id);
        return filmService.getFilmById(id);
    }
}