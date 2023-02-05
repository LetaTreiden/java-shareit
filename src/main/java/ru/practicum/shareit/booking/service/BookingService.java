package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;

import java.util.Collection;

public interface BookingService {
    BookingDTOToReturn add(Long userId, BookingDTO bookingDto);

    BookingDTOToReturn update(Long userId, Long bookingId, Boolean approved);

    BookingDTOToReturn get(Long bookingId, Long userId);

    Collection<BookingDTOToReturn> getByBooker(Long usersId, String state);

    Collection<BookingDTOToReturn> getByOwner(Long usersId, String status);
}
