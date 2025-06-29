package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UserResponse;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

public final class UserMapper {
    private UserMapper() {
        throw new UnsupportedOperationException("Это утилитарный класс, экземпляры создавать нельзя");
    }

    public static User mapFromCreateRequest(NewUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setLogin(request.getLogin());
        user.setName(request.getName() == null || request.getName().isBlank() ?
                request.getLogin() : request.getName());
        user.setBirthday(request.getBirthday());
        return user;
    }

    public static UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setLogin(user.getLogin());
        response.setName(user.getName());
        response.setBirthday(user.getBirthday());
        response.setFriends(user.getFriends() == null ? Collections.emptySet() : user.getFriends());
        return response;
    }

    public static List<UserResponse> mapToResponseList(Collection<User> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }

        List<UserResponse> result = new ArrayList<>(users.size());
        for (User user : users) {
            result.add(mapToResponse(user));
        }
        return result;
    }
}