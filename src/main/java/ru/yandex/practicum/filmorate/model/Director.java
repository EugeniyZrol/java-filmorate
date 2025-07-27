package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
public class Director {
    private Long id;

    @NotBlank(message = "Имя режиссёра не может быть пустым")
    private String name;
}
