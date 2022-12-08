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
                .item(booking.getItem())
                .booker(booking.getBooker())
                .bookingStatus(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDTO bookingDto) {
        Booking booking = new Booking();
        if (bookingDto.getId() != null) {
            booking.setId(bookingDto.getId());
        }

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(bookingDto.getItem());
        booking.setBooker(bookingDto.getBooker());

        if (bookingDto.getBookingStatus() == null) {
            booking.setStatus(BookingStatus.WAITING);
        } else {
            booking.setStatus(bookingDto.getBookingStatus());
        }

        return booking;
    }
}