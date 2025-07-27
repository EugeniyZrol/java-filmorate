package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import java.util.List;

public interface DirectorService {
    List<DirectorDto> findAll();

    DirectorDto getById(Long id);

    DirectorDto create(DirectorDto directorDto);

    DirectorDto update(DirectorDto directorDto);

    void delete(Long id);
}