package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                .orElseThrow(() -> new NotFoundException("Item not found"));
        User user = uRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!item.getAvailable()) {
            throw new BadRequestException("You can not book this item");
        }
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("You cannot book your item");
        }
        if (bookingDto.getEnd().isBefore(LocalDateTime.now()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now()) ||
                (bookingDto.getEnd().isBefore(bookingDto.getStart()) &&
                        !bookingDto.getEnd().equals(bookingDto.getStart()))) {
            throw new BadRequestException("Wrong date");
        }
        Booking booking = bRepository.save(BookingMapper.toBooking(bookingDto, item, user));
//        log.info(booking.toString());
        return BookingMapper.toBookingDtoFrom(booking);
    }

    @Transactional
    @Override
    public BookingDTOToReturn update(Long userId, Long bookingId, Boolean approved) {
        Booking booking;
        try {
            booking = bRepository.getReferenceById(bookingId);
            log.info(booking.toString());
        } catch (NullPointerException e) {
            throw new NotFoundException("Booking not found");
        }
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
    public List<BookingDTOToReturn> getByBooker(Long userId, String status, Integer page, Integer size) {
        Optional<User> booker = uRepository.findById(userId);
        Pageable pageable;
        if (booker.isEmpty()) {
            throw new NotFoundException("No rights");
        }
        User user = booker.get();
        if (page != null && size != null) {
            if (page < 0 || size < 0) {
                throw new BadRequestException("Wrong meaning page or size");
            }
            if (size == 0) {
                throw new BadRequestException("Size equals 0!");
            }
            pageable = PageRequest.of(page / size, size);
            return findBookingByBookerByPage(user, status, pageable);
        } else {
            return findBookingByBooker(user.getId(), status);
        }
    }

    private List<BookingDTOToReturn> findBookingByBooker(Long userId, String status) {
        User booker = uRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("No rights"));
        List<Booking> bookingsByBooker;
        State state = stateValidation(status);
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

    private List<BookingDTOToReturn> findBookingByBookerByPage(User booker, String status, Pageable pageable) {
        Page<Booking> bookingsPage;
        if (status == null || status.equals("")) {
            status = "ALL";
        }
        switch (status) {
            case "ALL":
                bookingsPage = bRepository.findByBookerOrderByStartDesc(booker, pageable);
                break;
            case "CURRENT":
                bookingsPage = bRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(booker,
                        LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookingsPage = bRepository.findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(booker,
                        LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookingsPage = bRepository.findByBookerAndStartAfterOrderByStartDesc(booker, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookingsPage = bRepository.findByBookerAndStatusOrderByStartDesc(booker, Status.WAITING, pageable);
                break;
            case "REJECTED":
                bookingsPage = bRepository.findByBookerAndStatusOrderByStartDesc(booker, Status.REJECTED, pageable);
                break;
            default:
                throw new StatusBadRequestException("Unknown state: UNSUPPORTED_STATUS");

        }
        return BookingMapper.mapToBookingDtoFrom(bookingsPage);

    }

    @Override
    public List<BookingDTOToReturn> getByOwner(Long userId, String status, Integer page, Integer size) {
        User owner = uRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Pageable pageable;
        if (page != null && size != null) {
            if (page < 0 || size < 0) {
                throw new BadRequestException("From or size is less than 0");
            }
            if (size == 0) {
                throw new BadRequestException("Size equals 0");
            }
            pageable = PageRequest.of(page / size, size);

            return findBookingByOwnerByPage(userId, status, pageable);
        } else {
            return findBookingByOwner(userId, status);
        }
    }

    private List<BookingDTOToReturn> findBookingByOwnerByPage(Long userId, String status, Pageable pageable) {
        if (uRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }
        List<Booking> bookingsByOwner;
        State state = stateValidation(status);
        switch (state) {
            case ALL:
                bookingsByOwner = bRepository.findByOwnerAll(userId, pageable);
                break;
            case CURRENT:
                bookingsByOwner = bRepository.findByOwnerAndCurrent(userId, LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookingsByOwner = bRepository.findByOwnerAndPast(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookingsByOwner = bRepository.findByUserAndFuture(userId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookingsByOwner = bRepository.findByOwnerAndByStatus(userId, String.valueOf(Status.WAITING), pageable);
                break;
            case REJECTED:
                bookingsByOwner = bRepository.findByOwnerAndByStatus(userId, String.valueOf(Status.REJECTED), pageable);
                break;
            default:
                throw new StatusBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDtoFrom(bookingsByOwner);
    }

    private List<BookingDTOToReturn> findBookingByOwner(Long userId, String status) {
        if (uRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }
        List<Booking> bookingsByOwner;
        State state = stateValidation(status);
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

    private State stateValidation(String state) {
        try {
            Enum.valueOf(State.class, state);
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new StatusBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
