package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestDTOWithItems extends RequestDTO {

    private Long id;

    private String description;

    private User requester;

    private LocalDateTime created;

    private List<ItemDTO> items;
}
