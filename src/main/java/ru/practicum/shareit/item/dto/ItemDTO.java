package ru.practicum.shareit.item.dto;

/**
 * TODO Sprint add-controllers.
 */
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private String request;
}