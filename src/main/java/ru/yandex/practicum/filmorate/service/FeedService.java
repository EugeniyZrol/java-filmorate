package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.UserFeedDto;

import java.util.List;

public interface FeedService {
    List<UserFeedDto> getUserFeed(Long userId);
}