package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.*;
import java.util.Collection;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

public interface UserService {
    @Validated(OnCreate.class)
    UserResponse create(@Valid NewUserRequest request);

    @Validated(OnUpdate.class)
    UserResponse update(@Valid UpdateUserRequest request);

    void delete(Long userId);

    UserResponse getUserById(Long userId);

    Collection<UserResponse> findAll();

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<UserResponse> getFriends(Long userId);

    List<UserResponse> getCommonFriends(Long userId1, Long userId2);

    List<FilmResponseDto> getRecommendations(Long userId);
}