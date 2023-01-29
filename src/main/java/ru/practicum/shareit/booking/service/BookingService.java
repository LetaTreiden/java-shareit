package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    BookingDTO create(Long id, BookingDTO booking);

    BookingDTO findBookingById(Long id, Long bId);

    BookingDTO confirmOrRejectBooking(Long id, Long bId, Boolean approved);

    List<Booking> findBookingByIdAndStatus(String state, Long id);

    List<Booking> findAllOwnersBookings(String state, Long id);

    Optional<Booking> getLastBooking(Long id);

    Optional<Booking> getNextBooking(Long id);

    boolean checkBooking(long userId, long itemId, BookingStatus status);
}