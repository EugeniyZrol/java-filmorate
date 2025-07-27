package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;
    private final DirectorMapper directorMapper;

    @Override
    public List<DirectorDto> findAll() {
        return directorStorage.findAll().stream()
                .map(directorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DirectorDto getById(Long id) {
        Director director = directorStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Режиссёр с ID " + id + " не найден."));
        return directorMapper.toDto(director);
    }

    @Override
    public DirectorDto create(DirectorDto directorDto) {
        Director director = directorMapper.toEntity(directorDto);
        Director createdDirector = directorStorage.create(director);
        return directorMapper.toDto(createdDirector);
    }

    @Override
    public DirectorDto update(DirectorDto directorDto) {
        Director director = directorMapper.toEntity(directorDto);
        Director updatedDirector = directorStorage.update(director)
                .orElseThrow(() -> new NotFoundException("Режиссёр с ID " + directorDto.getId() + " не найден."));
        return directorMapper.toDto(updatedDirector);
    }

    @Override
    public void delete(Long id) {
        directorStorage.delete(id);
    }
}