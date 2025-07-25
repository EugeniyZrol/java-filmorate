package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    Collection<FilmResponseDto> findAll();

    FilmResponseDto create(FilmRequestDto filmDto);

    FilmResponseDto update(FilmRequestDto filmDto);

    void delete(Long filmId);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<FilmResponseDto> getTopFilms(int count);

    FilmResponseDto getFilmById(Long id);

    List<FilmResponseDto> getCommonFilms(Long userId, Long friendId);
}