package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        if (users.values().stream().anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        log.info("Создание нового пользователя с логином: {}", user.getLogin());
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан: {}", user);
        return user;
    }

    @Override
    public User update(User newUser) {
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

    @Override
    public void delete(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        users.remove(userId);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
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