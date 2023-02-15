package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoTwo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
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

    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDtoTwo addBooking(Long userId, BookingDto bookingDto) {
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        Optional<User> user = userRepository.findById(userId);
        LocalDateTime now = LocalDateTime.now();
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (item.isPresent() && !item.get().getAvailable()) {
            throw new BadRequestException("Невозможно забронировать данную вещь");
        }
        if (item.isEmpty()) {
            throw new NotFoundException("Невозможно создать запрос на бронирование");
        }
        if (Objects.equals(item.get().getOwner().getId(), userId)) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }
        if (bookingDto.getEnd().isBefore(now) ||
                bookingDto.getStart().isBefore(now) ||
                (bookingDto.getEnd().isBefore(bookingDto.getStart()) &&
                        !bookingDto.getEnd().equals(bookingDto.getStart()))) {

            throw new BadRequestException("Невозможно создать запрос на бронирование c данной датой");
        }
        bookingDto.setBookerId(userId);
        bookingDto.setStatus(State.WAITING);
        Booking booking = repository.save(BookingMapper.toBooking(bookingDto, item.get(), user.get()));
        return BookingMapper.toBookingDtoFrom(booking);
    }

    @Transactional
    @Override
    public BookingDtoTwo updateStatusBooking(Long userId, Long bookingId, Boolean approved) {
        Optional<Booking> booking = repository.findById(bookingId);
        Booking booking1;
        if (booking.isEmpty()) {
            throw new NotFoundException("Отсутсвуют данные о бронировании");
        }
        booking1 = booking.get();
        Long ownerId = booking1.getItem().getOwner().getId();
        if (!Objects.equals(userId, ownerId)) {
            throw new NotFoundException("Отсутствует доступ для пользователя");
        }
        if (!Objects.equals(String.valueOf(booking1.getStatus()), "WAITING")) {
            throw new BadRequestException("Статус бронирования уже изменен");
        }
        if (approved) {
            booking1.setStatus(State.APPROVED);
        } else {
            booking1.setStatus(State.REJECTED);
        }
        return BookingMapper.toBookingDtoFrom(repository.save(booking1));
    }

    @Override
    public BookingDtoTwo getBooking(Long userId, Long bookingId) {
        Optional<Booking> booking = repository.findById(bookingId);
        long ownerId;
        long bookerId;
        if (booking.isPresent()) {
            ownerId = booking.get().getItem().getOwner().getId();
            bookerId = booking.get().getBooker().getId();
            if (Objects.equals(ownerId, userId) || Objects.equals(bookerId, userId)) {
                return BookingMapper.toBookingDtoFrom(booking.get());
            }
            throw new NotFoundException("Отсутствует доступ для пользователя");
        }
        throw new NotFoundException("Запрос на бронирование не найден");
    }

    @Override
    public Collection<BookingDtoTwo> getBookingByBooker(Long usersId, String status, Integer page, Integer size) {
        Optional<User> booker = userRepository.findById(usersId);
        Pageable pageable;
        if (booker.isEmpty()) {
            throw new NotFoundException("Для пользователя нет доступа");
        }
        User user = booker.get();
        if (page != null && size != null) {
            pageable = PageRequest.of(page / size, size);
            return findBookingByBookerByPage(user, status, pageable);
        } else {
            return findBookingByBooker(user, status);
        }

    }

    @Override
    public Collection<BookingDtoTwo> getBookingByOwner(Long usersId, String status, Integer page, Integer size) {
        Optional<User> owner = userRepository.findById(usersId);
        Pageable pageable;
        if (owner.isEmpty()) {
            throw new NotFoundException("Для пользователя нет доступа");
        }
        if (page != null && size != null) {
            pageable = PageRequest.of(page / size, size);
            return findBookingByOwnerByPage(usersId, status, pageable);
        } else {
            return findBookingByOwner(usersId, status);
        }
    }


    private Collection<BookingDtoTwo> findBookingByBookerByPage(User booker, String status, Pageable pageable) {
        Page<Booking> bookingsPage;
        switch (status) {
            case "CURRENT":
                bookingsPage = repository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(booker,
                        LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookingsPage = repository.findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(booker,
                        LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookingsPage = repository.findByBookerAndStartAfterOrderByStartDesc(booker, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookingsPage = repository.findByBookerAndStatusOrderByStartDesc(booker, State.WAITING, pageable);
                break;
            case "REJECTED":
                bookingsPage = repository.findByBookerAndStatusOrderByStartDesc(booker, State.REJECTED, pageable);
                break;
            default:
                bookingsPage = repository.findByBookerOrderByStartDesc(booker, pageable);
                break;
        }
        return BookingMapper.mapToBookingDtoFrom(bookingsPage);

    }

    private Collection<BookingDtoTwo> findBookingByBooker(User booker, String status) {
        List<Booking> bookingsByBooker;
        switch (status) {
            case "CURRENT":
                bookingsByBooker = repository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(booker,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookingsByBooker = repository.findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(booker,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "FUTURE":
                bookingsByBooker = repository.findByBookerAndStartAfterOrderByStartDesc(booker, LocalDateTime.now());
                break;
            case "WAITING":
                bookingsByBooker = repository.findByBookerAndStatusOrderByStartDesc(booker, State.WAITING);
                break;
            case "REJECTED":
                bookingsByBooker = repository.findByBookerAndStatusOrderByStartDesc(booker, State.REJECTED);
                break;
            default:
                bookingsByBooker = repository.findByBookerOrderByStartDesc(booker);
                break;
        }
        return BookingMapper.mapToBookingDtoFrom(bookingsByBooker);
    }

    private Collection<BookingDtoTwo> findBookingByOwnerByPage(Long usersId, String status, Pageable pageable) {
        List<Booking> bookings;
        switch (status) {
            case "CURRENT":
                bookings = repository.findByBookingForOwnerWithCurrent(usersId, LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookings = repository.findByBookingForOwnerWithPast(usersId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookings = repository.findByBookingForOwnerWithFuture(usersId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookings = repository.findByBookingForOwnerWithWaitingOrRejected(usersId, "WAITING",
                        pageable);
                break;
            case "REJECTED":
                bookings = repository.findByBookingForOwnerWithWaitingOrRejected(usersId, "REJECTED",
                        pageable);
                break;
            default:
                bookings = repository.findByBookingForOwnerWithAll(usersId, pageable);
                break;
        }
        return BookingMapper.mapToBookingDtoFrom(bookings);
    }

    private Collection<BookingDtoTwo> findBookingByOwner(Long usersId, String status) {
        List<Booking> bookingsByOwner;
        switch (status) {
            case "CURRENT":
                bookingsByOwner = repository.findByBookingForOwnerWithCurrent(usersId, LocalDateTime.now());
                break;
            case "PAST":
                bookingsByOwner = repository.findByBookingForOwnerWithPast(usersId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookingsByOwner = repository.findByBookingForOwnerWithFuture(usersId, LocalDateTime.now());
                break;
            case "WAITING":
                bookingsByOwner = repository.findByBookingForOwnerWithWaitingOrRejected(usersId, "WAITING");
                break;
            case "REJECTED":
                bookingsByOwner = repository.findByBookingForOwnerWithWaitingOrRejected(usersId, "REJECTED");
                break;
            default:
                bookingsByOwner = repository.findByBookingForOwnerWithAll(usersId);
                break;
        }
        return BookingMapper.mapToBookingDtoFrom(bookingsByOwner);
    }
}
