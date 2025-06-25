package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class FilmorateApplicationTest {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    void testFindUserById_shouldReturnUserWhenExists() {
        // 1. Создаем тестового пользователя
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testlogin");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));

        // 2. Сохраняем пользователя
        User createdUser = userStorage.create(testUser);
        Long userId = createdUser.getId();

        // 3. Ищем пользователя
        User foundUser = userStorage.findById(userId);

        // 4. Проверяем результаты
        assertNotNull(foundUser, "Пользователь должен быть найден");
        assertEquals(userId, foundUser.getId());
        assertEquals("test@example.com", foundUser.getEmail());
        assertEquals("testlogin", foundUser.getLogin());
        assertEquals("Test User", foundUser.getName());
        assertEquals(LocalDate.of(1990, 1, 1), foundUser.getBirthday());
    }

    @Test
    void testFindUserById_shouldThrowExceptionWhenNotExists() {
        // Проверяем, что выбрасывается исключение для несуществующего ID
        Long nonExistentId = 9999L;

        assertThrows(NotFoundException.class, () -> userStorage.findById(nonExistentId),
                "Должно быть выброшено NotFoundException для несуществующего пользователя");
    }
}