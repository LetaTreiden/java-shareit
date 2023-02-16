package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class RequestDtoGateway {

    private Long id;

    @NotBlank()
    private String description;

    private User requester;

    private LocalDateTime created;

    @Data
    public static class User {
        private final long id;
        private final String name;
    }
}
