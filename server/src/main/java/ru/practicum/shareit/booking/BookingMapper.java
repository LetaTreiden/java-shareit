package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoTwo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setItemId(booking.getItem() != null ? booking.getItem().getId() : null);
        bookingDto.setItemName(booking.getItem() != null ? booking.getItem().getName() : null);
        bookingDto.setBookerId(booking.getBooker() != null ? booking.getBooker().getId() : null);
        return bookingDto;
    }


    public static BookingDtoTwo toBookingDtoFrom(Booking booking) {
        BookingDtoTwo bookingDto = new BookingDtoTwo();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setItem(booking.getItem());
        bookingDto.setBooker(booking.getBooker());
        return bookingDto;
    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setId(bookingDto.getId());
        booking.setStatus(bookingDto.getStatus());
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }

    public static BookingDtoForItem toBookingDtoForItem(Booking booking, LocalDateTime dateTime) {
        BookingDtoForItem bookingDtoForItem = new BookingDtoForItem();
        bookingDtoForItem.setId(booking.getId());
        bookingDtoForItem.setBookerId(booking.getBooker().getId());
        bookingDtoForItem.setDateTime(dateTime);
        return bookingDtoForItem;
    }


    public static List<BookingDtoTwo> mapToBookingDtoFrom(Iterable<Booking> bookings) {
        List<BookingDtoTwo> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toBookingDtoFrom(booking));
        }
        return dtos;
    }
}
