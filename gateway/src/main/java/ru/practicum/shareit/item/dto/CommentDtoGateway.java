package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CommentDtoGateway {

    private Long id;

    private String text;

    private String itemName;

    private String authorName;

    private LocalDateTime created;
}
