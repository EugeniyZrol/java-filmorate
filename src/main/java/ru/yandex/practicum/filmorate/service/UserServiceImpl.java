package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
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

    @Override
    @Validated(OnCreate.class)
    public UserResponse create(@Valid NewUserRequest request) {
        User user = UserMapper.mapFromCreateRequest(request);
        User createdUser = userStorage.create(user);
        return UserMapper.mapToResponse(createdUser);
    }

    @Override
    @Validated(OnUpdate.class)
    public UserResponse update(@Valid UpdateUserRequest request) {
        User existingUser = userStorage.findById(request.getId());
        UserMapper.updateFromUpdateRequest(existingUser, request);
        User updatedUser = userStorage.update(existingUser);
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
        userStorage.removeFriend(userId, friendId);
    }

    @Override
    public List<UserResponse> getFriends(Long userId) {
        Set<Long> friendIds = userStorage.getFriends(userId);
        return friendIds.stream()
                .map(userStorage::findById)
                .map(UserMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CommonFriendsResponse getCommonFriends(Long userId1, Long userId2) {
        Set<Long> commonFriendIds = userStorage.getCommonFriends(userId1, userId2);

        Set<UserResponse> commonFriends = commonFriendIds.stream()
                .map(userStorage::findUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(UserMapper::mapToResponse)
                .collect(Collectors.toSet());

        return new CommonFriendsResponse(userId1, userId2, commonFriends);
    }

    private void validateFriendship(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("User cannot friend themselves");
        }
        if (!userStorage.exists(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        if (!userStorage.exists(friendId)) {
            throw new NotFoundException("User not found with id: " + friendId);
        }
    }
}
