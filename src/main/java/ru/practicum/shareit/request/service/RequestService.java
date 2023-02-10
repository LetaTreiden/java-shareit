package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.dto.RequestDTOWithItems;

import java.util.List;

public interface RequestService {
    RequestDTO add(Long userId, RequestDTO request);

    List<RequestDTOWithItems> findAllByOwner(Long userId);

    List<RequestDTOWithItems> findAll(Long userId, Integer page, Integer size);

    RequestDTO findById(Long userId, Long requestId);
}
