package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.modelGateway.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class RequestDtoGateway {

    private Long id;

    private String description;

    private User requester;

    private LocalDateTime created;

    @Data
    public static class User {
        private final long id;
        private final String name;
    }
}
