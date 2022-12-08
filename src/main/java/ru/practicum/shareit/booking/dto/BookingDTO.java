package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class BookingDTO {
    private Long id;
    @NonNull
    private LocalDateTime start;
    @NonNull
    private LocalDateTime end;
    @NonNull
    private Long item;
    private Long booker;
    private Long owner;
    private BookingStatus bookingStatus;

  /*  @Data
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
   */
}