package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface UserService {
    void addFriends(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Collection<User> getCommonFriends(Long userId1, Long userId2);

    Collection<User> getFriends(Long userId);

    User update(User user);

    User create(User user);

    void delete(Long userId);

    Collection<User> findAll();
}