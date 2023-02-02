package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
public class BookingDTOToList {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String itemName;
    private long itemId;
    private long bookerId;
    private BookingStatus status;
}
