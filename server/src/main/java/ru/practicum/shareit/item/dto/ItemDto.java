package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class ItemDto {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private Long requestId;

}
