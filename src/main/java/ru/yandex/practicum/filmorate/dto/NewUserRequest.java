package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.Birthday;
import ru.yandex.practicum.filmorate.validation.OnCreate;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    private String name;

    @Email(groups = OnCreate.class, message = "Имейл должен содержать @")
    @NotBlank(groups = OnCreate.class, message = "Имейл должен быть указан")
    private String email;

    @NotBlank(groups = OnCreate.class, message = "Логин не может быть пустым и содержать пробелы")
    private String login;

    @Birthday(groups = OnCreate.class, message = "Дата рождения не может быть из будущего")
    private LocalDate birthday;
}