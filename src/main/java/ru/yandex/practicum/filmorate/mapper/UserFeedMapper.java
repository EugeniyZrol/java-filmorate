package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserFeedDto;
import ru.yandex.practicum.filmorate.model.UserFeed;

@Component
public class UserFeedMapper {
    public static UserFeedDto toDto(UserFeed feed) {
        return new UserFeedDto(
                feed.getTimestamp(),
                feed.getUserId(),
                feed.getEventType().name(),
                feed.getOperation().name(),
                feed.getEventId(),
                feed.getEntityId()
        );
    }
}