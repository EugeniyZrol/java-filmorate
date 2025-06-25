package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.Birthday;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String login;

    private String name;

    @NotNull
    @Birthday
    private LocalDate birthday;
}