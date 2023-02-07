package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusBadRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        Item item = iRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        User user = uRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getAvailable()) {
            throw new BadRequestException("You can not book this item");
        }
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("You cannot book your item");
        }
        if ((!bookingDto.getEnd().isAfter(bookingDto.getStart()))) {
            throw new BadRequestException("Wrong date");
        }
        Booking booking = bRepository.save(BookingMapper.toBooking(bookingDto, item, user));
        return BookingMapper.toBookingDtoFrom(booking);
    }

    @Transactional
    @Override
    public BookingDTOToReturn update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = Optional.of(bRepository.getReferenceById(bookingId))
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        Long ownerId = booking.getItem().getOwner().getId();

        if (!Objects.equals(userId, ownerId)) {
            throw new NotFoundException("No rights");
        }

        if (!Objects.equals(String.valueOf(booking.getStatus()), "WAITING")) {
            throw new BadRequestException("Status has already been changed");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDtoFrom(booking);
    }

    @Override
    public BookingDTOToReturn get(Long userId, Long bookingId) {
        Booking booking = bRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(("Booking not found")));
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();
        if (Objects.equals(ownerId, userId) || Objects.equals(bookerId, userId)) {
            return BookingMapper.toBookingDtoFrom(booking);
        }
        throw new NotFoundException("No rights");
    }

    @Override
    public List<BookingDTOToReturn> getByBooker(Long usersId, String stateString) {
        User booker = uRepository.findById(usersId)
                .orElseThrow(() -> new NotFoundException("No rights"));
        List<Booking> bookingsByBooker;
        stateValidation(stateString);
        State state = State.valueOf(stateString);
        switch (state) {
            case ALL:
                bookingsByBooker = bRepository.findByBookerOrderByStartDesc(booker);
                break;
            case CURRENT:
                bookingsByBooker = bRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(booker,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookingsByBooker = bRepository.findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(booker,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case FUTURE:
                bookingsByBooker = bRepository.findByBookerAndStartAfterOrderByStartDesc(booker,
                        LocalDateTime.now());
                break;
            case WAITING:
                bookingsByBooker = bRepository.findByBookerAndStatusOrderByStartDesc(booker, Status.WAITING);
                break;
            case REJECTED:
                bookingsByBooker = bRepository.findByBookerAndStatusOrderByStartDesc(booker, Status.REJECTED);
                break;
            default:
                throw new StatusBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDtoFrom(bookingsByBooker);
    }

    @Override
    public List<BookingDTOToReturn> getByOwner(Long userId, String stateString) {
        if (uRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }
        List<Booking> bookingsByOwner;
        stateValidation(stateString);
        State state = State.valueOf(stateString);
        switch (state) {
            case ALL:
                bookingsByOwner = bRepository.findByOwnerAll(userId);
                break;
            case CURRENT:
                bookingsByOwner = bRepository.findByOwnerAndCurrent(userId, LocalDateTime.now());
                break;
            case PAST:
                bookingsByOwner = bRepository.findByOwnerAndPast(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookingsByOwner = bRepository.findByUserAndFuture(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingsByOwner = bRepository.findByOwnerAndByStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                bookingsByOwner = bRepository.findByOwnerAndByStatus(userId, Status.REJECTED);
                break;
            default:
                throw new StatusBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDtoFrom(bookingsByOwner);
    }

    private void stateValidation(String state) {
        try {
            Enum.valueOf(State.class, state);
        } catch (IllegalArgumentException e) {
            throw new StatusBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
