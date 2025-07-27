package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

@Component
public class DirectorMapper {
    public Director toEntity(DirectorDto dto) {
        return new Director(dto.getId(), dto.getName());
    }

    public DirectorDto toDto(Director director) {
        return new DirectorDto(director.getId(), director.getName());
    }
}