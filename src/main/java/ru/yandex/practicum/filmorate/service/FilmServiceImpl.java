package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmMapper filmMapper;
    private final MpaService mpaService;
    private final GenreService genreService;

    @Override
    public Collection<FilmResponseDto> findAll() {
        log.debug("Получение списка всех фильмов");
        Collection<FilmResponseDto> films = filmStorage.findAll().stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
        log.info("Возвращено {} фильмов", films.size());
        return films;
    }

    @Override
    @Transactional
    public FilmResponseDto create(@Validated(OnCreate.class) FilmRequestDto filmDto) {
        log.debug("Создание нового фильма: {}", filmDto);
        Film newFilm = prepareFilmFromDto(filmDto);
        Film savedFilm = filmStorage.create(newFilm);
        enrichFilmWithRelations(savedFilm);
        log.info("Фильм с ID {} успешно создан", savedFilm.getId());
        return filmMapper.toDto(savedFilm);
    }

    @Override
    @Transactional
    public FilmResponseDto update(@Validated(OnUpdate.class) FilmRequestDto filmDto) {
        log.debug("Обновление фильма с ID {}: {}", filmDto.getId(), filmDto);
        Film existingFilm = getExistingFilm(filmDto.getId());
        updateFilmFromDto(filmDto, existingFilm);
        Film updatedFilm = filmStorage.update(existingFilm);
        log.info("Фильм с ID {} успешно обновлен", updatedFilm.getId());
        return filmMapper.toDto(updatedFilm);
    }

    private Film prepareFilmFromDto(FilmRequestDto filmDto) {
        Film film = filmMapper.toEntity(filmDto);
        setMpaForFilm(filmDto, film);
        setGenresForFilm(filmDto, film);
        return film;
    }

    private void setMpaForFilm(FilmRequestDto filmDto, Film film) {
        if (filmDto.getMpa() != null) {
            validateMpa(filmDto.getMpa().getId());
            film.setMpa(new MpaRating(
                    filmDto.getMpa().getId(),
                    filmDto.getMpa().getName(),
                    filmDto.getMpa().getDescription()
            ));
        }
    }

    private void setGenresForFilm(FilmRequestDto filmDto, Film film) {
        if (filmDto.getGenres() != null && !filmDto.getGenres().isEmpty()) {
            List<Genre> genres = filmDto.getGenres().stream()
                    .map(this::convertToGenreEntity)
                    .distinct()
                    .collect(Collectors.toList());
            film.setGenres(genres);
        } else {
            film.setGenres(Collections.emptyList());
        }
    }

    private void updateFilmFromDto(FilmRequestDto dto, Film film) {
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());
        setMpaForFilm(dto, film);
        setGenresForFilm(dto, film);
    }

    private void enrichFilmWithRelations(Film film) {
        if (film.getMpa() != null) {
            MpaDto mpaDetails = mpaService.getMpaRatingById(film.getMpa().getId());
            film.getMpa().setName(mpaDetails.getName());
            film.getMpa().setDescription(mpaDetails.getDescription());
        }

        if (film.getGenres() != null) {
            List<Genre> enrichedGenres = film.getGenres().stream()
                    .map(genre -> genreService.getGenreById(genre.getId()))
                    .map(genreDto -> new Genre(genreDto.getId(), genreDto.getName()))
                    .collect(Collectors.toList());
            film.setGenres(enrichedGenres);
        }
    }

    private Film getExistingFilm(Long id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            log.error("Фильм с ID {} не найден", id);
            throw new NotFoundException("Фильм не найден");
        }
        return film;
    }

    private Genre convertToGenreEntity(GenreDto genreDto) {
        validateGenre(genreDto.getId());
        return new Genre(genreDto.getId(), genreDto.getName());
    }

    private void validateMpa(Integer mpaId) {
        if (!mpaService.mpaExists(mpaId)) {
            log.error("MPA с ID {} не найден", mpaId);
            throw new NotFoundException("MPA рейтинг не найден");
        }
    }

    private void validateGenre(Integer genreId) {
        if (!genreService.genreExists(genreId)) {
            log.error("Жанр с ID {} не найден", genreId);
            throw new NotFoundException("Жанр не найден");
        }
    }

    @Override
    public void delete(Long filmId) {
        log.debug("Удаление фильма с ID: {}", filmId);
        filmStorage.delete(filmId);
        log.info("Фильм с ID {} успешно удален", filmId);
    }

    @Override
    public FilmResponseDto getFilmById(Long id) {
        log.debug("Запрос фильма по ID: {}", id);
        FilmResponseDto film = filmMapper.toDto(filmStorage.findById(id));
        log.debug("Найден фильм: {}", film);
        return film;
    }

    @Override
    @Transactional
    public void addLike(Long filmId, Long userId) {
        log.debug("Попытка добавить лайк фильму {} от пользователя {}", filmId, userId);
        userStorage.findById(userId);
        Film film = filmStorage.findById(filmId);

        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь {} уже ставил лайк фильму {}", userId, filmId);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Пользователь уже ставил лайк");
        }
        try {
            filmStorage.addLike(filmId, userId);
            log.info("Лайк от пользователя {} фильму {} успешно добавлен", userId, filmId);
        } catch (DuplicateKeyException e) {
            log.warn("Пользователь {} уже ставил лайк фильму {}", userId, filmId);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Пользователь уже ставил лайк");
        }
    }

    @Override
    @Transactional
    public void removeLike(Long filmId, Long userId) {
        log.debug("Удаление лайка фильму {} от пользователя {}", filmId, userId);

        try {
            userStorage.findById(userId);
            Film film = filmStorage.findById(filmId);

            if (!film.getLikes().contains(userId)) {
                log.warn("Лайк не найден: фильм {}, пользователь {}", filmId, userId);
                throw new ConditionsNotMetException("Лайк не найден");
            }

            filmStorage.removeLike(filmId, userId);
            log.info("Удален лайк фильму {} от пользователя {}", filmId, userId);

        } catch (EmptyResultDataAccessException e) {
            log.error("Лайк не найден при удалении: film={}, user={}", filmId, userId, e);
            throw new ConditionsNotMetException("Лайк не найден");
        } catch (DataAccessException e) {
            log.error("Ошибка базы данных при удалении лайка: film={}, user={}", filmId, userId, e);
            throw new DataAccessResourceFailureException("Ошибка при удалении лайка", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilmResponseDto> getTopFilms(int count, Integer genreId, Integer year) {
        log.debug("Получение топ-{} фильмов по жанру {} и году {}", count, genreId, year);
        try {
            List<FilmResponseDto> topFilms = filmStorage.findTopFilms(count, genreId, year).stream()
                    .map(filmMapper::toDto)
                    .collect(Collectors.toList());
            log.info("Возвращено {} топовых фильмов", topFilms.size());
            return topFilms;
        } catch (DataAccessException e) {
            log.error("Ошибка при получении топовых фильмов", e);
            throw new DataRetrievalFailureException("Не удалось получить топовые фильмы", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilmResponseDto> getCommonFilms(Long userId, Long friendId) {
        log.debug("Получение общих фильмов пользователей: userId={}, friendId={}", userId, friendId);

        // Проверка существования пользователей
        userStorage.findById(userId);
        userStorage.findById(friendId);

        List<Film> commonFilms = filmStorage.findCommonFilms(userId, friendId);
        List<FilmResponseDto> result = commonFilms.stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());

        log.info("Найдено {} общих фильмов", result.size());
        return result;
    }
}