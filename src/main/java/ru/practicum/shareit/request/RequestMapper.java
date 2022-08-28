package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Component
public class RequestMapper {
    public ItemRequestDTO toItemDto(ItemRequest item) {
        return ItemRequestDTO.builder()
                .id(item.getId())
                .description(item.getDescription())
                .requester(toUserItemRequest(item.getRequester()))
                .created(item.getCreated())
                .build();
    }

    public ItemRequest toItem(ItemRequestDTO itemDto) {
        return ItemRequest.builder()
                .id(itemDto.getId())
                .description(itemDto.getDescription())
                .requester(toUser(itemDto.getRequester()))
                .created(itemDto.getCreated())
                .build();
    }

    private ItemRequestDTO.User toUserItemRequest(User user) {
        return ItemRequestDTO.User.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).build();
    }

    private User toUser(ItemRequestDTO.User bookingUser) {
        return User.builder().id(bookingUser.getId()).name(bookingUser.getName()).email(bookingUser.getEmail()).build();
    }
}