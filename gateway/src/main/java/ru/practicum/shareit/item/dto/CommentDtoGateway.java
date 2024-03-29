package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CommentDtoGateway {

    private Long id;

    @Size(min = 1, max = 1000)
    @NotBlank
    private String text;

    private String itemName;

    private String authorName;

    private LocalDateTime created;
}
