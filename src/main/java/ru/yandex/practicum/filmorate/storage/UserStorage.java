package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import ru.yandex.practicum.filmorate.model.User;


public interface UserStorage {
    User create(User user);

    User update(User user);

    void delete(Long userId);

    User findById(Long userId);

    Collection<User> findAll();

    boolean exists(Long userId);

    Optional<User> findUserById(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Set<Long> getFriends(Long userId);

    Set<Long> getCommonFriends(Long userId1, Long userId2);
}