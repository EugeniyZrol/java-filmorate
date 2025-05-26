package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addFriends(Long userId, Long friendId) {
        User user = userStorage.findAll().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User friend = userStorage.findAll().stream()
                .filter(u -> u.getId().equals(friendId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь с id = {} добавил в друзья пользователя с id = {}", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.findAll().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        User friend = userStorage.findAll().stream()
                .filter(u -> u.getId().equals(friendId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));
//        Код проверки наличия дружбы, тесты не проходит
//        if (!user.getFriends().contains(friendId)) {
//            throw new NotFoundException("Дружба между пользователями с id = " + userId + " и " + friendId + " не найдена");
//        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь с id = {} удалил пользователя с id = {} из друзей", userId, friendId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        User user1 = userStorage.findAll().stream()
                .filter(u -> u.getId().equals(userId1))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId1 + " не найден"));
        User user2 = userStorage.findAll().stream()
                .filter(u -> u.getId().equals(userId2))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId2 + " не найден"));
        Set<Long> commonFriendsIds = new HashSet<>(user1.getFriends());
        commonFriendsIds.retainAll(user2.getFriends());

        return commonFriendsIds.stream()
                .map(id -> userStorage.findAll().stream()
                        .filter(user -> user.getId().equals(id))
                        .findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        User user = userStorage.findAll().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        Set<Long> friendIds = user.getFriends();
        return friendIds.stream()
                .map(id -> userStorage.findAll().stream()
                        .filter(u -> u.getId().equals(id))
                        .findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}