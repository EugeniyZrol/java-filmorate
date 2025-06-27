package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.Birthday;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @NotNull
    private Long id;

    private String name;

    @NotBlank(groups = OnUpdate.class, message = "Имейл должен быть указан")
    private String email;

    @NotBlank(groups = OnUpdate.class, message = "Логин не может быть пустым и содержать пробелы")
    private String login;

    @Birthday(groups = OnUpdate.class, message = "Дата рождения не может быть из будущего")
    private LocalDate birthday;
}