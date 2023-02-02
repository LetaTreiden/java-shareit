package ru.practicum.shareit.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingDTOToList;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@Validated
public class BookingController {

    private final BookingServiceImpl bookingService;
    Logger logger = LoggerFactory.getLogger("log");

    @Autowired
    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDTO saveBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                  @Valid @RequestBody BookingDTO booking) {
        logger.info("запрос отправлен");
        return bookingService.create(id, booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDTO findBookingById(@RequestHeader("X-Sharer-User-Id") Long id,
                                   @PathVariable Long bookingId) {
        return bookingService.findBookingById(id, bookingId);
    }

    @GetMapping
    public List<Booking> findAllBookingsById(@RequestParam(defaultValue = "ALL") String state,
                                             @RequestHeader("X-Sharer-User-Id") Long id) {
        List<Booking> result = bookingService.findBookingByIdAndStatus(state, id);
        logger.info("done " + result);
        return result;
    }

    @PatchMapping("/{bookingId}")
    public BookingDTO confirmOrRejectBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        return bookingService.confirmOrRejectBooking(id, bookingId, approved);
    }

    @GetMapping("/owner")
    public List<BookingDTOToList> findAllOwnersBookings(@RequestParam(defaultValue = "ALL") String state,
                                                        @RequestHeader("X-Sharer-User-Id") Long id) {
        return bookingService.findAllOwnersBookings(state, id);
    }

}