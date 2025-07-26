package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FilmMapper filmMapper;

    @Override
    public UserResponse create(@Validated(OnCreate.class) NewUserRequest request) {
        User user = UserMapper.mapFromCreateRequest(request);
        User createdUser = userStorage.create(user);
        return UserMapper.mapToResponse(createdUser);
    }

    @Override
    public UserResponse update(@Validated(OnUpdate.class) UpdateUserRequest request) {
        log.info("Обновление пользователя с ID: {}", request.getId());
        User existingUser = userStorage.findById(request.getId());

        if (request.getEmail() != null) existingUser.setEmail(request.getEmail());
        if (request.getLogin() != null) existingUser.setLogin(request.getLogin());
        if (request.getName() != null) existingUser.setName(request.getName());
        if (request.getBirthday() != null) existingUser.setBirthday(request.getBirthday());

        User updatedUser = userStorage.update(existingUser);
        log.debug("Пользователь с ID {} успешно обновлен", updatedUser.getId());
        return UserMapper.mapToResponse(updatedUser);
    }

    @Override
    public void delete(Long userId) {
        userStorage.delete(userId);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userStorage.findById(userId);
        return UserMapper.mapToResponse(user);
    }

    @Override
    public Collection<UserResponse> findAll() {
        return UserMapper.mapToResponseList(userStorage.findAll());
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        validateFriendship(userId, friendId);
        userStorage.addFriend(userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);
        userStorage.removeFriend(userId, friendId);
    }

    @Override
    public List<UserResponse> getFriends(Long userId) {
        Set<Long> friendIds = userStorage.getFriends(userId);
        if (friendIds.isEmpty()) {
            return Collections.emptyList();
        }
        return friendIds.stream()
                .map(userStorage::findById)
                .map(UserMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getCommonFriends(Long userId1, Long userId2) {
        validateUsersExist(userId1, userId2);

        Set<Long> friends1 = userStorage.getFriends(userId1);
        Set<Long> friends2 = userStorage.getFriends(userId2);

        Set<Long> commonFriends = new HashSet<>(friends1);
        commonFriends.retainAll(friends2);

        return commonFriends.stream()
                .map(userStorage::findById)
                .map(UserMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateFriendship(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователь не может добавить самого себя в друзья");
        }
        if (!userStorage.exists(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (!userStorage.exists(friendId)) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }
    }

    private void validateUsersExist(Long userId1, Long userId2) {
        if (!userStorage.exists(userId1) || !userStorage.exists(userId2)) {
            throw new NotFoundException("Один или оба пользователя не найдены");
        }
    }

    @Override
    public List<FilmResponseDto> getRecommendations(Long userId) {
        if (!userStorage.exists(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        List<Long> recommendedFilmIds = userStorage.getRecommendedFilmIds(userId);
        return recommendedFilmIds.stream()
                .map(filmStorage::findById)
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }
}