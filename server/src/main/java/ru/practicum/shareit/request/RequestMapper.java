package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.dto.RequestDTOWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static RequestDTO toRequestDto(ItemRequest itemRequest) {
        RequestDTO requestDto = new RequestDTO();
        requestDto.setId(itemRequest.getId());
        requestDto.setDescription(itemRequest.getDescription());
        requestDto.setRequester(UserMapper.toUserToRequest(itemRequest.getRequester()));
        requestDto.setCreated(itemRequest.getCreated());
        return requestDto;
    }

    public static ItemRequest toItemRequest(RequestDTO requestDto) {
        ItemRequest request = new ItemRequest();
        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        request.setRequester(UserMapper.toUser(requestDto.getRequester()));
        request.setCreated(requestDto.getCreated());
        return request;
    }

    public static RequestDTOWithItems toRequestForFindDto(ItemRequest itemRequest, List<ItemDTO> item) {
        RequestDTOWithItems requestDto = new RequestDTOWithItems();
        requestDto.setId(itemRequest.getId());
        requestDto.setDescription(itemRequest.getDescription());
        requestDto.setRequester(UserMapper.toUserToRequest(itemRequest.getRequester()));
        requestDto.setCreated(itemRequest.getCreated());
        requestDto.setItems(item);
        return requestDto;
    }
}
