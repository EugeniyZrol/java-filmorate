package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class FilmMapper {
    public Film toEntity(FilmRequestDto dto) {
        Film film = new Film();
        film.setId(dto.getId());
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());

        if (dto.getDirectors() != null) {
            film.setDirectors(dto.getDirectors().stream()
                    .filter(Objects::nonNull)
                    .map(directorDto -> new Director(directorDto.getId(), directorDto.getName()))
                    .collect(Collectors.toList()));
        } else {
            film.setDirectors(new ArrayList<>()); // Инициализируем пустым списком
        }

        return film;
    }

    public FilmResponseDto toDto(Film film) {
        FilmResponseDto dto = new FilmResponseDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        if (film.getMpa() != null) {
            dto.setMpa(new MpaDto(
                    film.getMpa().getId(),
                    film.getMpa().getName(),
                    film.getMpa().getDescription()
            ));
        }

        dto.setGenres(film.getGenres() != null ?
                film.getGenres().stream()
                        .sorted(Comparator.comparingInt(Genre::getId))
                        .map(genre -> new GenreDto(genre.getId(), genre.getName()))
                        .collect(Collectors.toList()) :
                Collections.emptyList());

        dto.setDirectors(film.getDirectors() != null ?
                film.getDirectors().stream()
                        .map(director -> new DirectorDto(director.getId(), director.getName()))
                        .collect(Collectors.toList()) :
                Collections.emptyList());

        dto.setLikesCount(film.getLikes() != null ? film.getLikes().size() : 0);

        return dto;
    }
}