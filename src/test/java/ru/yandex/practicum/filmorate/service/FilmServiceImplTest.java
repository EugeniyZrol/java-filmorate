//package ru.yandex.practicum.filmorate.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.storage.FilmStorage;
//import ru.yandex.practicum.filmorate.storage.UserStorage;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class FilmServiceImplTest {
//    private FilmServiceImpl filmService;
//    private FilmStorage filmStorage;
//    private UserStorage userStorage;
//
//    @BeforeEach
//    void setUp() {
//        filmStorage = Mockito.mock(FilmStorage.class);
//        userStorage = Mockito.mock(UserStorage.class);
//        filmService = new FilmServiceImpl(filmStorage, userStorage);
//    }
//
//    @Test
//    void addLike_FilmExists_UserExists_LikeAdded() {
//        Long filmId = 1L;
//        Long userId = 1L;
//
//        User user = new User();
//        user.setId(userId);
//
//        Film film = new Film();
//        film.setId(filmId);
//        film.setLikes(new HashSet<>());
//
//        when(filmStorage.findById(filmId)).thenReturn(film);
//        when(userStorage.findById(userId)).thenReturn(user);
//
//        filmService.addLike(filmId, userId);
//
//        assertTrue(film.getLikes().contains(userId));
//
//        verify(filmStorage).findById(filmId);
//        verify(userStorage).findById(userId);
//    }
//
//    @Test
//    void addLike_FilmNotFound_ThrowsNotFoundException() {
//        Long filmId = 999L;
//        Long userId = 1L;
//        User user = new User();
//        user.setId(userId);
//
//        when(filmStorage.findById(filmId)).thenThrow(new NotFoundException("Фильм с id " + filmId + " не найден"));
//        when(userStorage.findById(userId)).thenReturn(user);
//
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.addLike(filmId, userId));
//        assertEquals("Фильм с id " + filmId + " не найден", exception.getMessage());
//    }
//
//    @Test
//    void addLike_UserNotFound_ThrowsNotFoundException() {
//        Long filmId = 1L;
//        Long userId = 999L;
//
//        Film film = new Film();
//        film.setId(filmId);
//        film.setLikes(new HashSet<>());
//
//        when(filmStorage.findById(filmId)).thenReturn(film);
//
//        when(userStorage.findById(userId)).thenThrow(new NotFoundException("Пользователь с id = " + userId + " не найден"));
//
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.addLike(filmId, userId));
//
//        assertEquals("Пользователь с id = " + userId + " не найден", exception.getMessage());
//    }
//
//    @Test
//    void removeLike_FilmExists_UserExists_LikeRemoved() {
//        Long filmId = 1L;
//        Long userId = 1L;
//
//        User user = new User();
//        user.setId(userId);
//
//        Film film = new Film();
//        film.setId(filmId);
//        film.setLikes(new HashSet<>(Collections.singletonList(userId))); // Устанавливаем лайк
//
//        when(filmStorage.findById(filmId)).thenReturn(film);
//        when(userStorage.findById(userId)).thenReturn(user);
//
//        filmService.removeLike(filmId, userId);
//
//        assertFalse(film.getLikes().contains(userId));
//
//        verify(filmStorage).findById(filmId);
//        verify(userStorage).findById(userId);
//    }
//
//    @Test
//    void removeLike_FilmNotFound_ThrowsNotFoundException() {
//        Long filmId = 999L;
//        Long userId = 1L;
//        User user = new User();
//        user.setId(userId);
//
//        // Настраиваем findById для пользователя
//        when(userStorage.findById(userId)).thenReturn(user);
//
//        // Настраиваем findById для фильма, чтобы выбрасывал исключение
//        when(filmStorage.findById(filmId)).thenThrow(new NotFoundException("Фильм с id " + filmId + " не найден"));
//
//        // Проверка, что исключение выбрасывается
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.removeLike(filmId, userId));
//        assertEquals("Фильм с id " + filmId + " не найден", exception.getMessage());
//    }
//
//    @Test
//    void removeLike_UserNotFound_ThrowsNotFoundException() {
//        Long filmId = 1L;
//        Long userId = 999L;
//
//        Film film = new Film();
//        film.setId(filmId);
//        film.setLikes(new HashSet<>()); // Не содержит лайков
//
//        // Настраиваем findById для фильма
//        when(filmStorage.findById(filmId)).thenReturn(film);
//
//        // Настраиваем findById для пользователя, чтобы выбрасывал исключение
//        when(userStorage.findById(userId)).thenThrow(new NotFoundException("Пользователь с id = " + userId + " не найден"));
//
//        // Проверка, что исключение выбрасывается
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.removeLike(filmId, userId));
//        assertEquals("Пользователь с id = " + userId + " не найден", exception.getMessage());
//    }
//
//    @Test
//    void getTopFilms_ReturnsTopFilms() {
//        Film film1 = new Film();
//        film1.setId(1L);
//        film1.setLikes(new HashSet<>(Arrays.asList(1L, 2L)));
//
//        Film film2 = new Film();
//        film2.setId(2L);
//        film2.setLikes(new HashSet<>(Collections.singletonList(1L)));
//
//        when(filmStorage.findAll()).thenReturn(Arrays.asList(film1, film2));
//
//        List<Film> topFilms = filmService.getTopFilms(10);
//
//        assertEquals(2, topFilms.size());
//        assertEquals(film1, topFilms.get(0)); // film1 должен быть первым
//        assertEquals(film2, topFilms.get(1)); // film2 должен быть вторым
//    }
//}