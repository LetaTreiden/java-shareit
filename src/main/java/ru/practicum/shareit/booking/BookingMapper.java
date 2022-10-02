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

    /*public Booking toBooking(BookingDTO bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getBookingStatus());
        booking.setBooker(bookingDto.getBooker());
        booking.setItem(bookingDto.getItem());
        return booking;
    }

    private static BookingDTO.Item toBookingItem(Item item) {
        return BookingDTO.Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    private static BookingDTO.User toUserBooking(User user) {
        return BookingDTO.User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

     */
}