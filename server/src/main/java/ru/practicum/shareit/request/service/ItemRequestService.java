package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(Long userId, ItemRequestDto request);

    List<RequestDto> findAllOwnRequest(Long userId);

    List<RequestDto> findAllRequest(Long userId, Integer page, Integer size);

    RequestDto findRequest(Long userId, Long requestId);
}
