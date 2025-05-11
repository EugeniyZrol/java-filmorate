package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class User {
    private Long id;
    private String name;
    @NonNull
    @Email
    private String email;
    @NonNull
    private String login;
    @NonNull
    private LocalDate birthday;
}