package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoTwo;

import java.util.Collection;

public interface BookingService {
    BookingDtoTwo addBooking(Long userId, BookingDto bookingDto);

    BookingDtoTwo updateStatusBooking(Long userId, Long bookingId, Boolean approved);

    BookingDtoTwo getBooking(Long bookingId, Long userId);

    Collection<BookingDtoTwo> getBookingByBooker(Long usersId, String state, Integer page, Integer size);

    Collection<BookingDtoTwo> getBookingByOwner(Long usersId, String status, Integer page, Integer size);
}
