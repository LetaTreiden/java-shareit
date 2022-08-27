package ru.practicum.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private String request;

}