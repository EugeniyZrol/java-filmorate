package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.Birthday;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class User {
    @NotNull(groups = OnUpdate.class, message = "Id должен быть указан")
    private Long id;

    private String name;

    @Email(groups = OnCreate.class, message = "Имейл должен содержать @")
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Имейл должен быть указан")
    private String email;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Логин не может быть пустым и содержать пробелы")
    private String login;

    @Birthday(groups = {OnUpdate.class, OnCreate.class}, message = "Дата рождения не может быть из будущего")
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();
}