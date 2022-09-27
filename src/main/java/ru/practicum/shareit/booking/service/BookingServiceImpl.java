package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDTO;

import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.InvalidParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bRepo;
    private final ItemRepository iRepo;
    private final UserRepository uRepo;

    @Autowired
    public BookingServiceImpl(BookingRepository bRepo, ItemRepository iRepo,
                              UserRepository uRepo) {
        this.bRepo = bRepo;
        this.iRepo = iRepo;
        this.uRepo = uRepo;
    }

    @Override
    public BookingDTO create(Long id, BookingDTO dto) {
        Booking booking = new Booking();
        if (validate(id, dto)) {
            booking.setItem(iRepo.getReferenceById(dto.getItem().getId()));
            booking.setStart(dto.getStart());
            booking.setEnd(dto.getEnd());
            booking.setBooker(uRepo.getReferenceById(id));
        }
        return BookingMapper.toBookingDto(bRepo.save(booking));
    }
    private boolean validate(Long id, BookingDTO dto) {

        if (!iRepo.existsById(dto.getItem().getId())) {
            throw new NotFoundException("Товар не найден");
        }
        if (id.equals(iRepo.getReferenceById(dto.getItem().getId()).getOwner().getId())) {
            throw new NotFoundException("Невозможно выполнить операцию");
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
                throw new InvalidParameterException("Item is unavailable");
            }
        }
        return true;
    }

    @Override
    public BookingDTO findBookingById(Long id, Long bId) {
        return null;
    }

    @Override
    public BookingDTO confirmOrRejectBooking(Long id, Long bId, Boolean approved) {
        return null;
    }

    @Override
    public List<BookingDTO> findBookingByIdAndStatus(String state, Long id) {
        return null;
    }

    @Override
    public List<BookingDTO> findAllOwnersBookings(String state, Long id) {
        return null;
    }
}
