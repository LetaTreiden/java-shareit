package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.InvalidParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Override
    public BookingDTO create(Long id, BookingDTO dto) {
        Booking booking = new Booking();
        validateUser(dto.getBooker().getId());
        validateUser(dto.getOwner().getId());
        if (validateItem(id, dto)) {
            booking.setItem(iRepo.getReferenceById(dto.getItem().getId()));
            booking.setStart(dto.getStart());
            booking.setEnd(dto.getEnd());
            booking.setBooker(uRepo.getReferenceById(id));
        }
        validateState(dto.getBookingStatus().toString());
        return BookingMapper.toBookingDto(bRepo.save(booking));
    }

    private boolean validateItem(Long id, BookingDTO dto) {

        if (!iRepo.existsById(dto.getItem().getId())) {
            throw new NotFoundException("Товар не найден");
        }
        if (id.equals(iRepo.getReferenceById(dto.getItem().getId()).getOwner().getId())) {
            throw new InvalidParameterException("Невозможно выполнить операцию");
        }
        if (dto.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidParameterException("Дата завершения не может находиться в прошлом");
        }
        if (dto.getEnd().isBefore(dto.getStart())) {
            throw new InvalidParameterException("Дата завершения не может быть раньше даты начала");
        }
        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new InvalidParameterException("Дата начала не может быть в прошлом");
        }
        if (!uRepo.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (iRepo.existsById(dto.getItem().getId())) {
            if (iRepo.getReferenceById(dto.getItem().getId()).getIsAvailable() == Boolean.FALSE) {
                throw new InvalidParameterException("Товар не доступен");
            }
        }
        return true;
    }

    private void validateUser(Long id) {
        if (!uRepo.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validateBooking(Long bookingId) {
        if (!bRepo.existsById(bookingId)) {
            throw new NotFoundException("Бронирование не найдено");
        }
    }

    private void validateState(String state) {
        if (!state.equals(BookingStatus.ALL.name()) && !state.equals(BookingStatus.REJECTED.name())
                && !state.equals(BookingStatus.WAITING.name()) && !state.equals(BookingStatus.CURRENT.name())
                && !state.equals(BookingStatus.APPROVED.name()) && !state.equals(BookingStatus.CANCELED.name())
                && !state.equals(BookingStatus.PAST.name()) && !state.equals(BookingStatus.FUTURE.name())) {
            throw new ValidationException("Неизвестный статус");
        }
    }


    @Override
    public Booking findBookingById(Long id, Long bId) {
        validateUser(id);

        Booking booking = bRepo.findById(bId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!Objects.equals(booking.getBooker().getId(), id)
                && !Objects.equals(booking.getItem().getOwner().getId(), id)) {
            throw new InvalidParameterException("Ошибка доступа");
        }

        return booking;
    }

    @Override
    public BookingDTO confirmOrRejectBooking(Long id, Long bId, Boolean approved) {

        validateBooking(bId);

        if (bRepo.getReferenceById(bId).getBooker().getId().equals(id)
                && approved && bRepo.getReferenceById(bId).getId().equals(bId)) {
            throw new NotFoundException("Товар не найден");
        }
        if (approved && bRepo.getReferenceById(bId).getStatus().equals(BookingStatus.APPROVED)
                && iRepo.getReferenceById(bRepo.getReferenceById(bId)
                .getItem().getId()).getOwner().getId().equals(id)) {
            throw new InvalidParameterException("Бронирование уже было подтверждено");
        }

        Booking booking = bRepo.getReferenceById(bId);

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bRepo.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<Booking> findBookingByIdAndStatus(String state, Long id) {
        validateUser(id);
        validateState(state);

        List<Booking> result = new ArrayList<>();

        BookingStatus status = BookingStatus.valueOf(state);

        if (status.equals(BookingStatus.ALL)) {
            result.addAll(bRepo.findBookingsByBookerId(id));

        } else if (status.equals(BookingStatus.CURRENT)) {
            result.addAll(bRepo.findBookingsByBookerIdWithCurrentStatus(id));

        } else if (status.equals(BookingStatus.PAST)) {
            result.addAll(bRepo.findBookingsByBookerIdWithPastStatus(id));

        } else if (status.equals(BookingStatus.FUTURE)) {
            result.addAll(bRepo.findBookingsByBookerIdWithFutureStatus(id));

        } else if (status.equals(BookingStatus.WAITING)) {
            result.addAll(bRepo.findBookingsByBookerIdWithWaitingOrRejectStatus(id, BookingStatus.WAITING));

        } else if (status.equals(BookingStatus.REJECTED)) {
            result.addAll(bRepo.findBookingsByBookerIdWithWaitingOrRejectStatus(id, BookingStatus.REJECTED));
        }
        return result;
    }

    @Override
    public List<Booking> findAllOwnersBookings(String state, Long id) {
        validateUser(id);
        validateState(state);

        List<Booking> result = new ArrayList<>();

        BookingStatus status = BookingStatus.valueOf(state);

        switch (status) {
            case ALL:
                result.addAll(bRepo.findAllOwnersBookings(id));
                break;
            case FUTURE:
                result.addAll(bRepo.findAllOwnersBookingsWithFutureStatus(id));
                break;
            case CURRENT:
                result.addAll(bRepo.findAllOwnersBookingsWithCurrentStatus(id));
                break;
            case PAST:
                result.addAll(bRepo.findAllOwnersBookingsWithPastStatus(id));
                break;
        }
        return result;
    }
}
