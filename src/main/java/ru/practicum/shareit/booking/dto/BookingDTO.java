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
    private Long itemId;
    @NonNull
    private Long bookerId;
    @NonNull
    private Long ownerId;
    private BookingStatus bookingStatus;
}