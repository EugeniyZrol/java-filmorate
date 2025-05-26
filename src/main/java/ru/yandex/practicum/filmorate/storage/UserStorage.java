package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserStorage {

    User update(User user);

    User create(User user);

    void delete(Long userId);

    Collection<User> findAll();
}