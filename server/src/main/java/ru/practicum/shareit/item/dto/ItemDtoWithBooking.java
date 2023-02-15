package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDtoWithBooking {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private Long request;

    private BookingDtoForItem nextBooking;

    private BookingDtoForItem lastBooking;

    private List<ItemDtoWithComment> comments = new ArrayList<>();

}
