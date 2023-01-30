package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.dto.UserDTO;

@Data
public class BookingDTOtoReturn {
    private long id;
    private long itemId;
    private UserDTO booker;
    private BookingStatus status;
}
