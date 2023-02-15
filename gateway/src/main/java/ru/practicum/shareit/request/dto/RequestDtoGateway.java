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

    @NotBlank
    @Size(min = 1, max = 1000)
    private String description;

    private User requestor;

    @FutureOrPresent
    private LocalDateTime created;
}
