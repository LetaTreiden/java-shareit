package ru.practicum.shareit.booking.dto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class BookingDTO {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ru.practicum.shareit.item.model.Item item;
   // private Long itemId;
    private ru.practicum.shareit.user.model.User booker;
    private Long bookerId;
  //  private User owner;
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