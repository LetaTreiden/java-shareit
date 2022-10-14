package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDTO;

@Component
public class BookingMapper {

    public static BookingDTO toBookingDto(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .bookingStatus(booking.getStatus())
                .build();
    }
}