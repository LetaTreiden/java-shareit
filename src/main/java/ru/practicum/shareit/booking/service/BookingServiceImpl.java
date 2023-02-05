package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.StatusBadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bRepository;
    private final ItemRepository iRepository;
    private final UserRepository uRepository;

    @Transactional
    @Override
    public BookingDTOToReturn add(Long userId, BookingDTO bookingDto) {
        Optional<Item> item = Optional.ofNullable(iRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("User not found")));
        Optional<User> user = Optional.ofNullable(uRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Item not found")));
        if (item.isPresent() && !item.get().getAvailable()) {
            throw new BadRequestException("You can not book this item");
        }
        if (Objects.equals(item.get().getOwner().getId(), userId)) {
            throw new NotFoundException("You cannot book your item");
        }
        if (bookingDto.getEnd().isBefore(LocalDateTime.now()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now()) ||
                (bookingDto.getEnd().isBefore(bookingDto.getStart()) &&
                        !bookingDto.getEnd().equals(bookingDto.getStart()))) {
            throw new BadRequestException("Wrong date");
        }
        bookingDto.setBookerId(userId);
        bookingDto.setStatus(State.WAITING);
        Booking booking = bRepository.save(BookingMapper.toBooking(bookingDto, item.get(), user.get()));
        return BookingMapper.toBookingDtoFrom(booking);
    }

    @Transactional
    @Override
    public BookingDTOToReturn update(Long userId, Long bookingId, Boolean approved) {
        Optional <Booking> booking = Optional.ofNullable(Optional.of(bRepository.getReferenceById(bookingId))
                .orElseThrow(() -> new NotFoundException("Booking not found")));
        Long ownerId = booking.get().getItem().getOwner().getId();

        if (!Objects.equals(userId, ownerId)) {
            throw new NotFoundException("No rights");
        }

        if (!Objects.equals(String.valueOf(booking.get().getStatus()), "WAITING")) {
            throw new BadRequestException("Status has already been changed");
        }

        if (approved) {
            booking.get().setStatus(State.APPROVED);
        } else {
            booking.get().setStatus(State.REJECTED);
        }
        return BookingMapper.toBookingDtoFrom(booking.get());
    }

    @Override
    public BookingDTOToReturn get(Long userId, Long bookingId) {
        log.info("start");
        Booking booking = Optional.of(bRepository.getReferenceById(bookingId))
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        log.info("continue" + booking.getId() + booking.getItem());
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();
            if (Objects.equals(ownerId, userId) || Objects.equals(bookerId, userId)) {
                log.info("end");
                return BookingMapper.toBookingDtoFrom(booking);
            }
            throw new NotFoundException("No rights");
    }

    @Override
    public List<BookingDTOToReturn> getByBooker(Long usersId, String status) {
        Optional<User> booker = Optional.ofNullable(uRepository.findById(usersId)
                .orElseThrow(() -> new NotFoundException("No rights")));
        List<Booking> bookingsByBooker;
        switch (status) {
            case "ALL":
                bookingsByBooker = bRepository.findByBookerOrderByStartDesc(booker.get());
                break;
            case "CURRENT":
                bookingsByBooker = bRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(booker.get(),
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookingsByBooker = bRepository.findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(booker.get(),
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "FUTURE":
                bookingsByBooker = bRepository.findByBookerAndStartAfterOrderByStartDesc(booker.get(),
                        LocalDateTime.now());
                break;
            case "WAITING":
                bookingsByBooker = bRepository.findByBookerAndStatusOrderByStartDesc(booker.get(), State.WAITING);
                break;
            case "REJECTED":
                bookingsByBooker = bRepository.findByBookerAndStatusOrderByStartDesc(booker.get(), State.REJECTED);
                break;
            default:
                throw new StatusBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDtoFrom(bookingsByBooker);
    }

    @Override
    public List<BookingDTOToReturn> getByOwner(Long userId, String status) {
        if (uRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }
        List<Booking> bookingsByOwner;
        switch (status) {
            case "ALL":
                bookingsByOwner = bRepository.findByOwnerAll(userId);
                break;
            case "CURRENT":
                bookingsByOwner = bRepository.findByOwnerAndCurrent(userId, LocalDateTime.now());
                break;
            case "PAST":
                bookingsByOwner = bRepository.findByOwnerAndPast(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookingsByOwner = bRepository.findByUserAndFuture(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookingsByOwner = bRepository.findByOwnerAndByStatus(userId, State.WAITING);
                break;
            case "REJECTED":
                bookingsByOwner = bRepository.findByOwnerAndByStatus(userId, State.REJECTED);
                break;
            default:
                throw new StatusBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDtoFrom(bookingsByOwner);
    }
}
