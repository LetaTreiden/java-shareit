package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDtoGateway {

    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
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
