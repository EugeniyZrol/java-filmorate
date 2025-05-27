package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public void delete(Long userId) {
        userStorage.delete(userId);
    }

    @Override
    public void addFriends(Long userId, Long friendId) {

        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        log.info("Пользователь с id = {} добавил в друзья пользователя с id = {}", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {

        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь с id = {} удалил пользователя с id = {} из друзей", userId, friendId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId1, Long userId2) {

        User user1 = userStorage.findById(userId1);
        User user2 = userStorage.findById(userId2);

        Set<Long> commonFriendsIds = new HashSet<>(user1.getFriends());
        commonFriendsIds.retainAll(user2.getFriends());

        List<User> commonFriends = new ArrayList<>();
        for (Long friendId : commonFriendsIds) {
            try {
                User commonFriend = userStorage.findById(friendId);
                commonFriends.add(commonFriend);
            } catch (NotFoundException e) {
                log.warn("Общий друг с id = {} не найден для пользователей с id = {} и {}", friendId, userId1, userId2);
            }
        }
        return commonFriends;
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        User user = userStorage.findById(userId);
        Set<Long> friendIds = user.getFriends();
        List<User> friends = new ArrayList<>();
        for (Long friendId : friendIds) {
            try {
                User friend = userStorage.findById(friendId);
                friends.add(friend);
            } catch (NotFoundException e) {
                log.warn("Друг с id = {} не найден для пользователя с id = {}", friendId, userId);
            }
        }
        return friends;
    }
}