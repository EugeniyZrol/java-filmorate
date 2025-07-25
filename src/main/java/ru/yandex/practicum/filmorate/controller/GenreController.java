package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Collection<GenreDto> getAllGenres() {
        log.info("Запрос всех жанров");
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public GenreDto getGenreById(@PathVariable int id) {
        log.info("Запрос жанра по id: {}", id);
        return genreService.getGenreById(id);
    }
}