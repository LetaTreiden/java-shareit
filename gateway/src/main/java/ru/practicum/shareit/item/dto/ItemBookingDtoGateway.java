package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemDtoGateway;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemBookingDtoGateway {
    private long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private Long request;

    private BookingItemDtoGateway nextBooking;

    private BookingItemDtoGateway lastBooking;

    private List<CommentDtoGateway> comments;

    @Data
    public static class User {
        private final long id;
        private final String name;
        private final String email;
    }
}
