package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoTwo;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoTwo addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody BookingDto bookingDto) {
        log.info("Добавление запроса на аренду пользователем с id {}", userId);
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoTwo updateStatusBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        log.info("Обновление статуса запроса на аренду с id {}", bookingId);
        return bookingService.updateStatusBooking(userId, bookingId, approved);

    }

    @GetMapping("/{bookingId}")
    public BookingDtoTwo getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Просмотр запроса на пренду с id {}", bookingId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDtoTwo> findBookingByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(required = false) String state,
                                                         @RequestParam(name = "from", required = false)
                                                         Integer from,
                                                         @RequestParam(name = "size", required = false)
                                                         Integer size) {
        log.info("Получение списка бронирований пользовалеля с id {}", userId);
        return bookingService.getBookingByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoTwo> findBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(required = false) String state,
                                                        @RequestParam(name = "from", required = false)
                                                        Integer from,
                                                        @RequestParam(name = "size", required = false)
                                                        Integer size) {
        log.info("Получение списка бронирований для всех вещей пользователя с id {}", userId);
        return bookingService.getBookingByOwner(userId, state, from, size);
    }

}
