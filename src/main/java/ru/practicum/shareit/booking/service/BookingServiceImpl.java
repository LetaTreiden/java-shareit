package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.InvalidParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.booking.BookingStatus.ALL;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bRepo;
    private final ItemRepository iRepo;
    private final UserRepository uRepo;

    @Autowired
    public BookingServiceImpl(BookingRepository bRepo, ItemRepository iRepo, UserRepository uRepo) {
        this.bRepo = bRepo;
        this.iRepo = iRepo;
        this.uRepo = uRepo;
    }

    private static void dateTimeCheck(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.equals(end) || start.isBefore(LocalDateTime.now()))
            throw new InvalidParameterException("Неправильно заданные временные параметры");
    }

    @Transactional
    @Override
    public BookingDTO create(Long bookerId, BookingDTO bookingDto) {
        if (!uRepo.existsById(bookerId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        dateTimeCheck(bookingDto.getStart(), bookingDto.getEnd());

        Long itemId = bookingDto.getItem().getId();
        if (!iRepo.existsById(itemId)) {
            throw new NotFoundException("Товар не найден");
        }
        Item item = iRepo.getReferenceById(itemId);

        if (item.getOwner().getId().equals(bookerId))
            throw new ValidationException("Владелец не может создать бронь на свою вещь");

        if (!item.getIsAvailable())
            throw new ValidationException("Вещь с указанным id недоступна для запроса на бронирование.");

        /*bookingDto.setBookingStatus(BookingStatus.WAITING);
        bookingDto.setBooker(uRepo.getReferenceById(bookerId));
        bookingDto.setOwner(item.getOwner());
        bRepo.save(BookingMapper.toBooking(bookingDto));

         */
        return bookingDto;
    }

    private void validateUser(Long id) {
        if (!uRepo.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validateItem(Long id) {
        if (!iRepo.existsById(id)) {
            throw new NotFoundException("Товар не найден");
        }
    }

    private void validateState(String state) {
        if (!state.equals(ALL.name())
                && !state.equals(BookingStatus.REJECTED.name())
                && !state.equals(BookingStatus.WAITING.name())
                && !state.equals(BookingStatus.CURRENT.name())
                && !state.equals(BookingStatus.APPROVED.name())
                && !state.equals(BookingStatus.CANCELED.name())
                && !state.equals(BookingStatus.PAST.name())
                && !state.equals(BookingStatus.FUTURE.name())) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public Booking findBookingById(Long id, Long bId) {
        validateUser(id);
        if (!bRepo.existsById(bId)) throw new NotFoundException("Бронь с таким id не существует");

        Booking booking = bRepo.getReferenceById(bId);
        Item item = iRepo.getReferenceById(booking.getItem().getId());
        if (!Objects.equals(item.getOwner().getId(), id) && !Objects.equals(booking.getBooker().getId(), id))
            throw new InvalidParameterException("Данный пользователь не может получить информацию о заданной вещи.");

        return booking;
    }

    @Override
    public BookingDTO confirmOrRejectBooking(Long id, Long bId, Boolean approved) {
        if (!uRepo.existsById(id)) throw new NotFoundException("Пользователя с таким id не существует");
        if (!bRepo.existsById(bId)) throw new NotFoundException("Брони с таким id не существует");
        Booking booking = bRepo.getReferenceById(bId);

        Item item = iRepo.getReferenceById(booking.getItem().getId());
        if (!Objects.equals(item.getOwner().getId(), id))
            throw new InvalidParameterException("Данный пользователь не может управлять запрашиваемой бронью.");

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            booking.setItem(item);
        } else booking.setStatus(BookingStatus.REJECTED);

        bRepo.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<Booking> findBookingByIdAndStatus(String state, Long id) {
        if (!uRepo.existsById(id)) throw new NotFoundException("Пользователя с таким id не существует");
        validateState(state);

        List<Booking> result = new ArrayList<>();

        switch (state) {
            case "ALL":
                result.addAll(bRepo.findBookingsByBookerId(id));

                break;
            case "CURRENT":
                result.addAll(bRepo.findBookingsByBookerIdWithCurrentStatus(id));

                break;
            case "PAST":
                result.addAll(bRepo.findBookingsByBookerIdWithPastStatus(id));

                break;
            case "FUTURE":
                result.addAll(bRepo.findBookingsByBookerIdWithFutureStatus(id));

                break;
            case "WAITING":
                result.addAll(bRepo.findBookingsByBookerIdWithWaitingOrRejectStatus(id, BookingStatus.WAITING));

                break;
            case "REJECTED":
                result.addAll(bRepo.findBookingsByBookerIdWithWaitingOrRejectStatus(id, BookingStatus.REJECTED));
                break;
        }
        result.sort(Comparator.comparing(Booking::getStart).reversed());
        return result;
    }

    @Override
    public List<Booking> findAllOwnersBookings(String state, Long id) {
        validateUser(id);
        validateState(state);

        BookingStatus status = BookingStatus.valueOf(state);
        List<Booking> result;

        switch (status) {
            case ALL:
                result = bRepo.findAllOwnersBookings(id);

                break;
            case CURRENT:
                result = bRepo.findAllOwnersBookingsWithCurrentStatus(id);

                break;
            case PAST:
                result = bRepo.findAllOwnersBookingsWithPastStatus(id);

                break;
            case FUTURE:
                result = bRepo.findAllOwnersBookingsWithFutureStatus(id);

                break;
            case WAITING:
                result = bRepo.findAllOwnersBookingsWithStatus(id, BookingStatus.WAITING);

                break;
            case REJECTED:
                result = bRepo.findAllOwnersBookingsWithStatus(id, BookingStatus.REJECTED);
                break;
            default:
                throw new InvalidParameterException("Неизвестный статус");
        }
        result.sort(Comparator.comparing(Booking::getStart).reversed());
        return result;
    }
}