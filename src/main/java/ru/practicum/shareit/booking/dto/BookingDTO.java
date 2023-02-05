package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.shareit.booking.model.State;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingDTO {

    private long id;

    @FutureOrPresent
    @NonNull
    private LocalDateTime start;

    @Future
    @NonNull
    private LocalDateTime end;

    @NonNull
    private Long itemId;

    private String itemName;

    private Long bookerId;

    private State status;
}
