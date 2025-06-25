package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Component
public class MpaMapper {

    public MpaDto toDto(MpaRating mpa) {
        if (mpa == null) {
            return null;
        }
        return new MpaDto(
                mpa.getId(),
                mpa.getName(),
                mpa.getDescription()
        );
    }
}