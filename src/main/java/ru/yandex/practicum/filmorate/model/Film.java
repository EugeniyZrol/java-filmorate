package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class Film {
    @NotNull(groups = OnUpdate.class, message = "Id должен быть указан")
    private Long id;

    @NotNull(groups = OnCreate.class, message = "Название фильма должно быть указано")
    @NotBlank(groups = OnCreate.class, message = "Название не может быть пустым")
    private String name;

    @NotNull(groups = OnCreate.class, message = "Описание фильма должно быть указано")
    @NotBlank(groups = OnCreate.class, message = "Описание не может быть пустым")
    @Size(max = 200, groups = {OnCreate.class, OnUpdate.class}, message = "Максимальная длина описания - 200 символов")
    private String description;

    @NonNull
    @ReleaseDate(groups = {OnCreate.class, OnUpdate.class}, message = "Дата не может быть раньше 28 декабря 1985 года.")
    private LocalDate releaseDate;

    @Positive(groups = {OnCreate.class, OnUpdate.class}, message = "Продолжительность фильма должна быть положительным числом")
    private Long duration;
}