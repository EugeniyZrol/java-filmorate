package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmRequestDto {
    @Null(groups = OnCreate.class, message = "Id должен быть null при создании")
    @NotNull(groups = OnUpdate.class, message = "Id должен быть указан")
    private Long id;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Size(max = 255, message = "Максимальная длина названия - 255 символов")
    private String name;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    @ReleaseDate(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    @Positive(message = "Продолжительность должна быть положительным числом")
    private int duration;

    private MpaDto mpa;
    private List<GenreDto> genres;
}