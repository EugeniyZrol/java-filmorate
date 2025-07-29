package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserFeedDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserFeedMapper;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedStorage feedStorage;
    private final UserStorage userStorage;

    @Override
    public List<UserFeedDto> getUserFeed(Long userId) {
        if (!userStorage.exists(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        List<UserFeed> feeds = feedStorage.findByUserId(userId);

        feeds.sort(Comparator.comparing(UserFeed::getTimestamp));

        return feeds.stream()
                .map(UserFeedMapper::toDto)
                .collect(Collectors.toList());
    }
}