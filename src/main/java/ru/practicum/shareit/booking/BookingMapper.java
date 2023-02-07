package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingDTOForItem;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
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
        bookingDto.setItem(ItemMapper.toItemToBookingDTO(booking.getItem()));
        bookingDto.setBooker(UserMapper.toUserToBookingDTO(booking.getBooker()));
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

    public static BookingDTOForItem toBookingDtoForItem(Booking booking) {
        BookingDTOForItem bookingDtoForItem = new BookingDTOForItem();
        bookingDtoForItem.setId(booking.getId());
        bookingDtoForItem.setBookerId(booking.getBooker().getId());
        return bookingDtoForItem;
    }


    public static List<BookingDTOToReturn> mapToBookingDtoFrom(Iterable<Booking> bookings) {
        List<BookingDTOToReturn> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toBookingDtoFrom(booking));
        }
        return dtos;
    }
}
