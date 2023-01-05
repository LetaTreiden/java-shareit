package ru.practicum.shareit.booking.dto;
import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.model.User;

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
    private ru.practicum.shareit.item.model.Item item;
    private User booker;
    private User owner;
    private BookingStatus bookingStatus;

    @Data
    @Builder
    @Getter
    @Setter
    public static class Item {
        private Long id;
        private String name;
        private boolean available;
    }

    @Data
    @Builder
    @Getter
    @Setter
    public static class UserDTO extends User {
        private Long id;
        private String name;
     //   private String email;
    }
}