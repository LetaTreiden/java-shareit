package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoGateway;
import ru.practicum.shareit.booking.dto.StateGateway;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingControllerGateway {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> findByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(required = false, defaultValue = "ALL") String state,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                   Integer from,
                                               @PositiveOrZero @RequestParam(name = "size", defaultValue = "10")
                                                   Integer size) {
        StateGateway stateGateway = StateGateway.from(state);
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, stateGateway, from, size);
    }

    @Data
    @AllArgsConstructor
    static class HttpError {
        String error;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                  Integer from,
                                              @PositiveOrZero @RequestParam(name = "size", defaultValue = "10")
                                                  Integer size) {
        StateGateway stateGateway = StateGateway.from(state);
        log.info("Get booking for owner with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getByOwner(userId, stateGateway, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestBody @Valid BookingDtoGateway bookingDto) {
        log.info("Creating booking {}, userId={}", bookingDto, userId);
        return bookingClient.bookingItem(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable Long bookingId,
                                         @RequestParam Boolean approved) {
        log.info("Update status bookingId {} for userId={}", bookingId, userId);
        return bookingClient.bookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.get(userId, bookingId);
    }
}
