package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;

import java.util.Collection;

public interface BookingService {
    BookingDTOToReturn addBooking(Long userId, BookingDTO bookingDto);

    BookingDTOToReturn updateStatusBooking(Long userId, Long bookingId, Boolean approved);

    BookingDTOToReturn getBooking(Long bookingId, Long userId);

    Collection<BookingDTOToReturn> getBookingByBooker(Long usersId, String state);

    Collection<BookingDTOToReturn> getBookingByOwner(Long usersId, String status);
}
