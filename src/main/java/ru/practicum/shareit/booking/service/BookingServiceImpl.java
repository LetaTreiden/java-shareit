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
        validateItem(dto);
        validateBooking(id, dto);

        booking.setItem(iRepo.getReferenceById(dto.getItem().getId()));
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setBooker(uRepo.getReferenceById(id));

        validateState(dto.getBookingStatus().toString());
        return BookingMapper.toBookingDto(bRepo.save(booking));
    }

    private void validateItem(BookingDTO dto) {
        if (!iRepo.existsById(dto.getItem().getId())) {
            throw new NotFoundException("Товар не найден");
        }
    }

    private void validateUser(Long id) {
        if (!uRepo.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validateBooking(Long id, BookingDTO dto) {
        if (id.equals(iRepo.getReferenceById(dto.getItem().getId()).getOwner().getId())) {
            throw new NotFoundException("Невозможно совершить действие");
        }
        if (dto.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidParameterException("Дата конца в прошлом");
        }
        if (dto.getEnd().isBefore(dto.getStart())) {
            throw new InvalidParameterException("Дата начала после даты конца");
        }
        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new InvalidParameterException("Дата начала в прошлом");
        }
        if (!uRepo.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (iRepo.existsById(dto.getItem().getId())) {
            if (iRepo.getReferenceById(dto.getItem().getId()).getIsAvailable() == Boolean.FALSE) {
                throw new InvalidParameterException("Товар недоступен для аренды");
            }
        }
    }

    private void validateState(String state) {
        if (!state.equals(BookingStatus.ALL.name()) && !state.equals(BookingStatus.REJECTED.name()) &&
                !state.equals(BookingStatus.WAITING.name()) && !state.equals(BookingStatus.CURRENT.name()) &&
                !state.equals(BookingStatus.APPROVED.name()) && !state.equals(BookingStatus.CANCELED.name()) &&
                !state.equals(BookingStatus.PAST.name()) && !state.equals(BookingStatus.FUTURE.name())) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }


    @Override
    public Booking findBookingById(Long id, Long bId) {
        validateUser(id);
        validateBookingExist(bId);
        Booking booking = bRepo.getReferenceById(bId);
        if (!Objects.equals(booking.getBooker().getId(), id) &&
                !Objects.equals(booking.getItem().getOwner().getId(), id)) {
            throw new InvalidParameterException("Ошибка доступа");
        }
        return booking;
    }

    @Override
    public BookingDTO confirmOrRejectBooking(Long id, Long bId, Boolean approved) {
        validateBookingExist(bId);
        if (bRepo.getReferenceById(bId).getBooker().getId().equals(id) && approved &&
                bRepo.getReferenceById(bId).getId().equals(bId)) {
            throw new NotFoundException("Товар не найден");
        }
        if (approved && bRepo.getReferenceById(bId).getStatus().equals(BookingStatus.APPROVED) &&
                iRepo.getReferenceById(bRepo.getReferenceById(bId).getItem().getId()).getOwner().getId().equals(id)) {
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

    private void validateBookingExist(long id) {
        if (!bRepo.existsById(id)) {
            throw new NotFoundException("Такого бронирования не существует");
        }
    }

    @Override
    public List<Booking> findBookingByIdAndStatus(String state, Long id) {
        validateUser(id);
        validateState(state);

        List<Booking> result = new ArrayList<>();
        BookingStatus status = BookingStatus.valueOf(state);

        switch (status) {
            case ALL:
                result.addAll(bRepo.findBookingsByBookerId(id));

                break;
            case CURRENT:
                result.addAll(bRepo.findBookingsByBookerIdWithCurrentStatus(id));

                break;
            case PAST:
                result.addAll(bRepo.findBookingsByBookerIdWithPastStatus(id));

                break;
            case FUTURE:
                result.addAll(bRepo.findBookingsByBookerIdWithFutureStatus(id));

                break;
            case WAITING:
                result.addAll(bRepo.findBookingsByBookerIdWithWaitingOrRejectStatus(id, BookingStatus.WAITING));

                break;
            case REJECTED:
                result.addAll(bRepo.findBookingsByBookerIdWithWaitingOrRejectStatus(id, BookingStatus.REJECTED));
                break;
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
