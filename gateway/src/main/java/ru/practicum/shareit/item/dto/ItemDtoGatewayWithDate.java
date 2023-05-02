package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemDtoGatewayWithDate {
    private Long id;

    private String text;

    private String itemName;

    private String authorName;

    private LocalDateTime created;
}
