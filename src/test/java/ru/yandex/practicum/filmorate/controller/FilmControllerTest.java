package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void createFilm_WithEmptyName_ShouldThrowException() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(120L);

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class, () ->
                filmController.create(film));

        Assertions.assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    void createFilm_WithDescriptionExceeding200Characters_ShouldThrowException() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("a".repeat(201)); // Создаем строку из 201 символа
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(120L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                filmController.create(film));

        Assertions.assertEquals("Превышена Максимальная длина описания", exception.getMessage());
    }

    @Test
    void createFilm_WithReleaseDateBeforeMin_ShouldThrowException() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // Дата до 28 декабря 1895
        film.setDuration(120L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                filmController.create(film));

        Assertions.assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    void createFilm_WithNegativeDuration_ShouldThrowException() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(-10L); // Негативная продолжительность

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                filmController.create(film));

        Assertions.assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
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