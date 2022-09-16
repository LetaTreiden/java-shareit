package ru.practicum.shareit.item.model;

/**
 * TODO Sprint add-controllers.
 */
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private String request;
    private User owner;
}