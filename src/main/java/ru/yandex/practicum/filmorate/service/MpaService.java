package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Collection;


public interface MpaService {

    Collection<MpaDto> getAllMpaRatings();

    MpaDto getMpaRatingById(int id) throws NotFoundException;

    boolean mpaExists(int mpaId);
}
