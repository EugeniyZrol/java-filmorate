package ru.yandex.practicum.filmorate.model;

import lombok.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class Film {
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    private Long duration;
}