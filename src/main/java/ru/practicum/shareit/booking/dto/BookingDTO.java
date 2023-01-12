package ru.practicum.shareit.booking.dto;
import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDTO;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class BookingDTO {
    @NonNull
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private UserDTO booker;
    private BookingStatus bookingStatus;

}