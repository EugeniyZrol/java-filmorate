package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmServiceImpl(filmStorage, userStorage);
        filmController = new FilmController(filmService);
    }


    @Test
    void createFilm_WithValidData_ShouldReturnFilm() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(120L);

        Film createdFilm = filmController.create(film);

        Assertions.assertNotNull(createdFilm.getId());
        Assertions.assertEquals("Название", createdFilm.getName());
        Assertions.assertEquals("Описание", createdFilm.getDescription());
        Assertions.assertEquals(LocalDate.of(2023, 1, 1), createdFilm.getReleaseDate());
        Assertions.assertEquals(120, createdFilm.getDuration());
    }
}