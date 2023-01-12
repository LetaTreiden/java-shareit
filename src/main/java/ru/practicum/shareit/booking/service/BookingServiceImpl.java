package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.State;
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
        Long itemId = bookingDto.getItem().getId();
        if (!iRepo.existsById(itemId)) {
            throw new NotFoundException("Товар не найден");
        }
        Item item = iRepo.getReferenceById(itemId);

        if (!uRepo.existsById(bookerId) || item.getOwner().getId().equals(bookerId)) {
            throw new ValidationException("Пользователь yе может создать бронь");
        }
        dateTimeCheck(bookingDto.getStart(), bookingDto.getEnd());

        if (!item.getIsAvailable())
            throw new ValidationException("Вещь с указанным id недоступна для запроса на бронирование.");
        bRepo.save(BookingMapper.toBooking(bookingDto));
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
        if (!state.equals(State.REJECTED.name())
                && !state.equals(State.ALL.name())
                && !state.equals(State.PAST.name())
                && !state.equals(State.CURRENT.name())
                && !state.equals(State.FUTURE.name())
                && !state.equals(State.WAITING.name())
        ) {
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
        Booking booking = bRepo.findById(bId)
                .orElseThrow(() -> new NotFoundException("Заявка на аренду не найдена"));
        if (!Objects.equals(booking.getItem().getOwner().getId(), id)) {
            throw new NotFoundException("Не найден пользователь с правом на обновление статуса заявки");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Нельзя изменять статус заявки после подтверждения");
        }
        if (approved && bookingUpdateStatusValidator(booking, id)) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bRepo.save(booking));
    }

    private boolean bookingUpdateStatusValidator(Booking booking, long ownerId) {
        LocalDateTime startTime = booking.getStart();
        LocalDateTime endTime = booking.getEnd();
        List<Booking> approvedBookingsFutureOrPresent = bRepo
                .findAllOwnersBookingsWithStatus(ownerId, BookingStatus.APPROVED);
        if (approvedBookingsFutureOrPresent.isEmpty()) {
            return true;
        }
        return approvedBookingsFutureOrPresent.stream()
                .anyMatch(booking1 -> (startTime.isBefore(booking1.getStart()) && endTime.isBefore(booking1.getStart()))
                        || (startTime.isAfter(booking1.getEnd()) && endTime.isAfter(booking1.getEnd()))
                );
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

        State status = State.valueOf(state);
        List<Booking> result;

        switch (status) {
            case WAITING:
                result = bRepo.findAllOwnersBookingsWithWaitingStatus(id);
                break;
            case REJECTED:
                result = bRepo.findAllOwnersBookingsWithRejectedState(id);
                break;
            case ALL:
                result = bRepo.findAllOwnersBookings(id);
                break;
            case PAST:
                result = bRepo.findAllOwnersBookingsWithPastState(id);
                break;
            case FUTURE:
                result = bRepo.findAllOwnersBookingsWithFutureStatus(id);
                break;
            case CURRENT:
                result = bRepo.findAllOwnersBookingsWithCurrentStatus(id);
                break;
            default:
                throw new InvalidParameterException("Неизвестный статус");
        }
        result.sort(Comparator.comparing(Booking::getStart).reversed());
        return result;
    }
}