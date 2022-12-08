package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {

    public static BookingDTO toBookingDto(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItemId())
                .bookerId(booking.getBookerId())
                .bookingStatus(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDTO bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItemId(bookingDto.getItemId());
        booking.setBookerId(bookingDto.getBookerId());

        if (bookingDto.getBookingStatus() == null) {
            booking.setStatus(BookingStatus.WAITING);
        } else {
            booking.setStatus(bookingDto.getBookingStatus());
        }

        return booking;
    }
}
