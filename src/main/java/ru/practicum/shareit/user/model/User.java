package ru.practicum.shareit.user.model;

/**
 * TODO Sprint add-controllers.
 */
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long id;
    private String email;
    private String name;
}