package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class BookingDTO {
    @NonNull
    private Long id;
    @NonNull
    private LocalDateTime start;
    @NonNull
    private LocalDateTime end;
    @NonNull
    private ru.practicum.shareit.item.model.Item item;
    @NonNull
    private ru.practicum.shareit.user.model.User booker;
    @NonNull
    private User owner;
    private BookingStatus bookingStatus;

    @Data
    @Builder
    @Getter
    @Setter
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private boolean available;
        private Long requestId;
    }

    @Data
    @Builder
    @Getter
    @Setter
    public static class User {
        private Long id;
        private String name;
        private String email;
    }
}