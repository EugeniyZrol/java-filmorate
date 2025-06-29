package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.OnCreate;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserResponse> findAll() {
        log.info("GET /users - Получение списка всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable Long userId) {
        log.info("GET /users/{} - Получение пользователя по ID", userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Validated(OnCreate.class) @RequestBody NewUserRequest request) {
        log.info("POST /users - Создание нового пользователя: {}", request);
        return userService.create(request);
    }

    @PutMapping
    public UserResponse updateUser(@Validated(OnUpdate.class) @RequestBody UpdateUserRequest request) {
        log.info("PUT /users - Обновление пользователя: {}", request);
        return userService.update(request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("DELETE /users/{} - Удаление пользователя", userId);
        userService.delete(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable Long userId,
                          @PathVariable Long friendId) {
        log.info("PUT /users/{}/friends/{} - Добавление друга", userId, friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable Long userId,
                             @PathVariable Long friendId) {
        log.info("DELETE /users/{}/friends/{} - Удаление друга", userId, friendId);
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<?> getFriends(@PathVariable Long userId) {
        log.info("GET /users/{}/friends - Получение списка друзей", userId);
        try {
            userService.getUserById(userId);
            List<UserResponse> friends = userService.getFriends(userId);
            return ResponseEntity.ok(friends);
        } catch (NotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Not Found",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<UserResponse> getCommonFriends(
            @PathVariable Long userId,
            @PathVariable Long otherId
    ) {
        log.info("GET /users/{}/friends/common/{} - Поиск общих друзей", userId, otherId);
        return userService.getCommonFriends(userId, otherId);
    }
}