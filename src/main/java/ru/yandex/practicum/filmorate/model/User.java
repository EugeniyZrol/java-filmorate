package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.validation.Birthday;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class User {
    @NotNull(groups = OnUpdate.class, message = "Id должен быть указан")
    private Long id;

    private String name;

    @NonNull
    @Email(groups = OnCreate.class, message = "Имейл должен содержать @")
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Имейл должен быть указан")
    private String email;

    @NonNull
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Логин не может быть пустым и содержать пробелы")
    private String login;

    @NonNull
    @Birthday(groups = {OnUpdate.class, OnCreate.class}, message = "Дата рождения не может быть из будущего")
    private LocalDate birthday;
}