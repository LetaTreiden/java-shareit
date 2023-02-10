package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
public class BookingDTOToReturn {

    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Item item;

    private User booker;

    private Status status;

    @Data
    public static class User {
        private final long id;
        private final String name;
        private final String email;
    }

    @Data
    public static class Item {
        private final long id;
        private final String name;
        private final String description;
    }
}