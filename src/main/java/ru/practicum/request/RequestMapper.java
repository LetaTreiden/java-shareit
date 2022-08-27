package ru.practicum.request;

import org.springframework.stereotype.Component;
import ru.practicum.user.User;

@Component
public class RequestMapper {
    public RequestDTO toItemDto(Request item) {
        return RequestDTO.builder()
                .id(item.getId())
                .description(item.getDescription())
                .requester(toUserItemRequest(item.getRequester()))
                .created(item.getCreated())
                .build();
    }

    public Request toItem(RequestDTO itemDto) {
        return Request.builder()
                .id(itemDto.getId())
                .description(itemDto.getDescription())
                .requester(toUser(itemDto.getRequester()))
                .created(itemDto.getCreated())
                .build();
    }

    private RequestDTO.User toUserItemRequest(User user) {
        return RequestDTO.User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private User toUser(RequestDTO.User bookingUser) {
        return User.builder()
                .id(bookingUser.getId())
                .name(bookingUser.getName())
                .email(bookingUser.getEmail())
                .build();
    }
}