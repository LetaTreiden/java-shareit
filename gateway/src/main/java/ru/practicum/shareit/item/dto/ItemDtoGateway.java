package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.modelGateway.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ItemDtoGateway {

    private long id;

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @NotBlank
    @Size(min = 1, max = 512)
    private String description;

    @NotNull
    private Boolean available;

    private User owner;

    private Long requestId;
}
