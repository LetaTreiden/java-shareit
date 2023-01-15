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
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private User booker;
    private BookingStatus bookingStatus;
}