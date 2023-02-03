package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.State;

import java.time.LocalDateTime;

@Data
public class BookingDTO {

    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private String itemName;

    private Long bookerId;

    private State status;
}
