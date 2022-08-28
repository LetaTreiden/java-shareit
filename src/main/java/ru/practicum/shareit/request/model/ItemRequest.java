package ru.practicum.shareit.request.model;

/**
 * TODO Sprint add-item-requests.
 */
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}

