package ru.practicum.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.user.User;

@Data
@Builder
public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private String request;
}