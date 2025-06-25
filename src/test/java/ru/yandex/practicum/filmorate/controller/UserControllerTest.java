//package ru.yandex.practicum.filmorate.controller;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.Assertions;
//import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.service.UserService;
//import ru.yandex.practicum.filmorate.service.UserServiceImpl;
//import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
//
//import java.time.LocalDate;
//
//class UserControllerTest {
//
//    private UserController userController;
//
//    @BeforeEach
//    void setUp() {
//        InMemoryUserStorage userStorage = new InMemoryUserStorage();
//        UserService userService = new UserServiceImpl(userStorage);
//        userController = new UserController(userService);
//    }
//
//    @Test
//    void createUser_withValidData_shouldCreateUser() {
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setLogin("testLogin");
//        user.setBirthday(LocalDate.of(2000, 1, 1));
//
//        User createdUser = userController.create(user);
//
//        Assertions.assertNotNull(createdUser);
//        Assertions.assertEquals("test@example.com", createdUser.getEmail());
//        Assertions.assertEquals("testLogin", createdUser.getLogin());
//    }
//
//    @Test
//    void createUser_withDuplicateEmail_shouldThrowException() {
//        User user1 = new User();
//        user1.setEmail("test@example.com");
//        user1.setLogin("testLogin1");
//        user1.setBirthday(LocalDate.of(2000, 1, 1));
//        userController.create(user1);
//
//        User user2 = new User();
//        user2.setEmail("test@example.com");
//        user2.setLogin("testLogin2");
//        user2.setBirthday(LocalDate.of(2000, 1, 1));
//
//        DuplicatedDataException exception = Assertions.assertThrows(DuplicatedDataException.class, () -> {
//            userController.create(user2);
//        });
//
//        Assertions.assertEquals("Этот имейл уже используется", exception.getMessage());
//    }
//
//    @Test
//    void updateUser_withValidData_shouldUpdateUser() {
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setLogin("testLogin");
//        user.setBirthday(LocalDate.of(2000, 1, 1));
//        User createdUser = userController.create(user);
//
//        User updatedUser = new User();
//        updatedUser.setId(createdUser.getId());
//        updatedUser.setEmail("new@example.com");
//        updatedUser.setLogin("newLogin");
//        updatedUser.setBirthday(LocalDate.of(1990, 1, 1));
//
//        User result = userController.update(updatedUser);
//        Assertions.assertEquals("new@example.com", result.getEmail());
//        Assertions.assertEquals("newLogin", result.getLogin());
//    }
//
//    @Test
//    void updateUser_withNonExistentId_shouldThrowException() {
//        User user = new User();
//        user.setId(999L); // Не существующий ID
//
//        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
//            userController.update(user);
//        });
//
//        Assertions.assertEquals("Пользователь с id = 999 не найден", exception.getMessage());
//    }
//}