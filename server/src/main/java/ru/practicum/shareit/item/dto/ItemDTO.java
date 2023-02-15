package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDTO {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private Long requestId;

    @Data
    public static class User {
        private final long id;
        private final String name;
        private final String email;
    }
}
