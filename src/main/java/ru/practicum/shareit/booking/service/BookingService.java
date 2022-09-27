package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDTO;

import java.util.List;

public interface BookingService {

    BookingDTO create(Long id, BookingDTO dto);

    BookingDTO findBookingById(Long id, Long bId);

    BookingDTO confirmOrRejectBooking(Long id, Long bId, Boolean approved);

    List<BookingDTO> findBookingByIdAndStatus(String state, Long id);

    List<BookingDTO> findAllOwnersBookings(String state, Long id);

}