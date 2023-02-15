package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toRequestDto(ItemRequest itemRequest) {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(itemRequest.getId());
        requestDto.setDescription(itemRequest.getDescription());
        requestDto.setRequestor(itemRequest.getRequestor());
        requestDto.setCreated(itemRequest.getCreated());
        return requestDto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto requestDto) {
        ItemRequest request = new ItemRequest();
        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        request.setRequestor(requestDto.getRequestor());
        request.setCreated(requestDto.getCreated());
        return request;
    }

    public static RequestDto toRequestForFindDto(ItemRequest itemRequest, List<ItemDto> item) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(itemRequest.getId());
        requestDto.setDescription(itemRequest.getDescription());
        requestDto.setRequestor(itemRequest.getRequestor());
        requestDto.setCreated(itemRequest.getCreated());
        requestDto.setItems(item);
        return requestDto;
    }

}
