package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingDTOForItem;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class BookingMapper {

    public static BookingDTOToReturn toBookingDtoFrom(Booking booking) {
        BookingDTOToReturn bookingDto = new BookingDTOToReturn();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setItem((booking.getItem()));
        bookingDto.setBooker((booking.getBooker()));
        return bookingDto;
    }

    public static Booking toBooking(BookingDTO bookingDto, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setId(bookingDto.getId());
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }

    public static BookingDTOForItem toBookingDtoForItem(long id, long bookerId) {
        BookingDTOForItem bookingDtoForItem = new BookingDTOForItem();
        bookingDtoForItem.setId(id);
        bookingDtoForItem.setBookerId(bookerId);
        return bookingDtoForItem;
    }


    public static List<BookingDTOToReturn> mapToBookingDtoFrom(Iterable<Booking> bookings) {
        List<BookingDTOToReturn> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toBookingDtoFrom(booking));
        }
        return dtos;
    }

    public static BookingDTO toBookingDto(Booking booking) {
        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setItemId(booking.getItem() != null ? booking.getItem().getId() : null);
        bookingDto.setItemName(booking.getItem() != null ? booking.getItem().getName() : null);
        bookingDto.setBookerId(booking.getBooker() != null ? booking.getBooker().getId() : null);
        return bookingDto;
    }
}
