package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;
    private final GenreMapper genreMapper;

    public List<GenreDto> getAllGenres() {
        List<Genre> genres = genreStorage.findAll();
        return genres.stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toList());
    }

    // Получить жанр по ID (с проверкой)
    public GenreDto getGenreById(int id) {
        Genre genre = genreStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр не найден!"));
        return genreMapper.toDto(genre);
    }

    public boolean genreExists(long genreId) {
        return genreStorage.findById((int) genreId).isPresent();
    }
}