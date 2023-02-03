package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
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

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bRepository;
    private final ItemRepository iRepository;
    private final UserRepository uRepository;

    @Transactional
    @Override
    public BookingDTOToReturn addBooking(Long userId, BookingDTO bookingDto) {
        Optional<Item> item = iRepository.findById(bookingDto.getItemId());
        Optional<User> user = uRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        if (item.isPresent() && !item.get().getAvailable()) {
            throw new BadRequestException("You can not book this item");
        }
        if (item.isEmpty()) {
            throw new NotFoundException("Cannot create booking");
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
    public BookingDTOToReturn updateStatusBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking;
        if (bRepository.existsById(bookingId)) {
            booking = bRepository.getReferenceById(bookingId);
        } else {
            throw new NotFoundException("Booking is empty");
        }
        Long ownerId = booking.getItem().getOwner().getId();

        if (!Objects.equals(userId, ownerId)) {
            throw new NotFoundException("No rights");
        }

        if (!Objects.equals(String.valueOf(booking.getStatus()), "WAITING")) {
            throw new BadRequestException("Status has already been changed");
        }

        if (approved) {
            booking.setStatus(State.APPROVED);
        } else {
            booking.setStatus(State.REJECTED);
        }
        return BookingMapper.toBookingDtoFrom(bRepository.save(booking));
    }

    @Override
    public BookingDTOToReturn getBooking(Long userId, Long bookingId) {
        Booking booking;
        if (bRepository.existsById(bookingId)) {
            booking = bRepository.getReferenceById(bookingId);
        } else {
            throw new NotFoundException("Booking not found");
        }
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();
            if (Objects.equals(ownerId, userId) || Objects.equals(bookerId, userId)) {
                return BookingMapper.toBookingDtoFrom(booking);
            }
            throw new NotFoundException("No rights");
    }

    @Override
    public Collection<BookingDTOToReturn> getBookingByBooker(Long usersId, String status) {
        Optional<User> booker = uRepository.findById(usersId);
        List<Booking> bookingsByBooker;
        if (booker.isEmpty()) {
            throw new NotFoundException("No rights");
        }
        if (status == null || status.equals("")) {
            status = "ALL";
        }
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
    public Collection<BookingDTOToReturn> getBookingByOwner(Long usersId, String status) {
        Optional<User> owner = uRepository.findById(usersId);
        List<Booking> bookingsByOwner;
        if (owner.isEmpty()) {
            throw new NotFoundException("No rights");
        }
        if (status == null || status.equals("")) {
            status = "ALL";
        }
        switch (status) {
            case "ALL":
                bookingsByOwner = bRepository.findByBookingForOwnerWithAll(usersId);
                break;
            case "CURRENT":
                bookingsByOwner = bRepository.findByBookingForOwnerWithCurrent(usersId, LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case "PAST":
                bookingsByOwner = bRepository.findByBookingForOwnerWithPast(usersId, LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case "FUTURE":
                bookingsByOwner = bRepository.findByBookingForOwnerWithFuture(usersId, LocalDateTime.now());
                break;
            case "WAITING":
                bookingsByOwner = bRepository.findByBookingForOwnerWithWaitingOrRejected(usersId, "WAITING");
                break;
            case "REJECTED":
                bookingsByOwner = bRepository.findByBookingForOwnerWithWaitingOrRejected(usersId, "REJECTED");
                break;
            default:
                throw new StatusBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDtoFrom(bookingsByOwner);
    }
}
