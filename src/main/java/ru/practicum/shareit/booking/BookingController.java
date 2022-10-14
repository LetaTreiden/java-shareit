package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/bookings")
public class BookingController {

    private final BookingServiceImpl bookingService;

    @Autowired
    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDTO saveBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                  @RequestBody BookingDTO bookingDto) {
        return bookingService.create(id, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public Booking findBookingById(@RequestHeader("X-Sharer-User-Id") Long id,
                                   @PathVariable Long bookingId) {
        return bookingService.findBookingById(id, bookingId);
    }

    @GetMapping
    public List<Booking> findAllBookingsById(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader("X-Sharer-User-Id") Long id) {

        return bookingService.findBookingByIdAndStatus(state, id);
    }

    @PatchMapping("/{bookingId}")
    public BookingDTO confirmOrRejectBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        return bookingService.confirmOrRejectBooking(id, bookingId, approved);
    }

    @GetMapping("/owner")
    public List<Booking> findAllOwnersBookings(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestHeader("X-Sharer-User-Id") Long id) {

        return bookingService.findAllOwnersBookings(state, id);
    }

}
