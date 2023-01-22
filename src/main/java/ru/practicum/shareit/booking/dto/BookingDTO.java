package ru.practicum.shareit.booking.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.dto.UserDTO;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long item;
    private UserDTO booker;
    private BookingStatus status;
}