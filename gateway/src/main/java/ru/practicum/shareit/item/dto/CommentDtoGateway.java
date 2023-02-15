package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CommentDtoGateway {

    private Long id;

    @Size(min = 1, max = 1000)
    private String text;

    @Size(min = 1, max = 100)
    private String itemName;

    @Size(min = 1, max = 100)
    private String authorName;

    @FutureOrPresent
    private LocalDateTime created;
}
