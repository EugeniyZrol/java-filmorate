package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;
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
    public User create(@Validated(OnCreate.class) @RequestBody User user) {

        if (users.values().stream().anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()))) {
            log.error("Этот имейл уже используется");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя для отображения не указано, будет использован логин: {}", user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан: {}", user);
        return user;
    }


    @PutMapping
    public User update(@Validated(OnUpdate.class) @RequestBody User newUser) {

        if (!users.containsKey(newUser.getId())) {
            log.error("Пользователь с id = {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        User oldUser = users.get(newUser.getId());

        if (users.values().stream().anyMatch(existingUser -> existingUser.getEmail().equals(newUser.getEmail())
                && !existingUser.getId().equals(newUser.getId()))) {
            log.error("Этот имейл уже используется: {}", newUser.getEmail());
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        oldUser.setEmail(newUser.getEmail());
        log.info("Имейл успешно обновлен");

        oldUser.setLogin(newUser.getLogin());
        log.info("Логин успешно обновлен");

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            oldUser.setName(newUser.getLogin());
            log.info("Имя для отображения не указано, будет использован логин: {}", newUser.getLogin());
        } else {
            oldUser.setName(newUser.getName());
        }

        oldUser.setBirthday(newUser.getBirthday());
        log.info("Дата рождения обновлена");

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