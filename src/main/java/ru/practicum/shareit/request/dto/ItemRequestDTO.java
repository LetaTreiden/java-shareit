package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDTO {

    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;

    @Data
    @Builder
    public static class User {
        private Long id;
        private String name;
        private String email;
    }
}