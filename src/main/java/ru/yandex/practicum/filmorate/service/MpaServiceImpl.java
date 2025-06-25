package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;
    private final MpaMapper mpaMapper;

    @Override
    public Collection<MpaDto> getAllMpaRatings() {
        log.info("Получение списка всех рейтингов MPA");
        return mpaStorage.findAll().stream()
                .map(mpaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MpaDto getMpaRatingById(int id) throws NotFoundException {
        log.info("Получение рейтинга MPA по id: {}", id);
        MpaRating mpa = mpaStorage.findById(id);
        if (mpa == null) {
            throw new NotFoundException("Рейтинг MPA с id " + id + " не найден");
        }
        return mpaMapper.toDto(mpa);
    }

    @Override
    public boolean mpaExists(int mpaId) {
        try {
            getMpaRatingById(mpaId);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }
}