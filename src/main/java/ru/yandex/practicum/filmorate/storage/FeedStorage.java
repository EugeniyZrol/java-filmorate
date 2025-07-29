package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.UserFeed;

import java.util.List;

public interface FeedStorage {
    UserFeed create(UserFeed feed);

    List<UserFeed> findByUserId(Long userId);
}