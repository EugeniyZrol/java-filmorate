package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Вызов метода findAll для получения всех фильмов.");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Имейл должен быть указан и содержать @");
            throw new ConditionsNotMetException("Имейл должен быть указан и содержать @");
        }

        if (users.values().stream().anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()))) {
            log.error("Этот имейл уже используется");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        if (user.getLogin().isBlank() || user.getLogin().trim().isEmpty()) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ConditionsNotMetException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя для отображения не указано, будет использован логин: {}", user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть из будущего");
            throw new ConditionsNotMetException("Дата рождения не может быть из будущего");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан: {}", user);
        return user;
    }


    @PutMapping
    public User update(@Valid @RequestBody User newUser) {

        if (newUser.getId() == null) {
            log.error("Id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            log.error("Пользователь с id = {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        User oldUser = users.get(newUser.getId());

        if (!newUser.getEmail().isBlank()) {
            if (users.values().stream().anyMatch(existingUser -> existingUser.getEmail().equals(newUser.getEmail())
                    && !existingUser.getId().equals(newUser.getId()))) {
                log.error("Этот имейл уже используется: {}", newUser.getEmail());
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            oldUser.setEmail(newUser.getEmail());
        }

        if (!newUser.getLogin().isBlank()) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            oldUser.setName(newUser.getLogin());
            log.info("Имя для отображения не указано, будет использован логин: {}", newUser.getLogin());
        } else {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getBirthday().isBefore(LocalDate.now())) {
            oldUser.setBirthday(newUser.getBirthday());
        } else {
            log.error("Дата рождения не может быть из будущего");
            throw new ConditionsNotMetException("Дата рождения не может быть из будущего");
        }

        log.info("Пользователь с id = {} обновлен", newUser.getId());
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}