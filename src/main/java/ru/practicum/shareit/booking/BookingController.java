package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDTOToReturn add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody BookingDTO bookingDto) {
        log.info("Добавление запроса на аренду пользователем с id {}", userId);
        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDTOToReturn update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        log.info("Обновление статуса запроса на аренду с id {}", bookingId);
        return bookingService.update(userId, bookingId, approved);

    }

    @GetMapping("/{bookingId}")
    public BookingDTOToReturn get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Просмотр запроса на пренду с id {}", bookingId);
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDTOToReturn> findByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(defaultValue = "ALL")
                                                         String status) {
        log.info("Получение списка бронирований пользовалеля с id {}", userId);
        return bookingService.getByBooker(userId, status);
    }

    @GetMapping("/owner")
    public Collection<BookingDTOToReturn> findByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "ALL")
                                                        String status) {
        log.info("Получение списка бронирований для всех вещей пользователя с id {}", userId);
        return bookingService.getByOwner(userId, status);
    }

}
