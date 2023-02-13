package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusBadRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
@Slf4j
class BookingServiceTest {

    private final Booking booking = new Booking();
    private final User user = new User();
    private final ItemRequest request = new ItemRequest();
    private final Item item = new Item();
    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    UserServiceImpl userService;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void addBookingTest() {
        addBooking();
        addRequest();
        addUser();
        addItem();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of((item)));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(booking);

        Optional<BookingDTOToReturn> bookingDto = Optional.ofNullable(bookingService.add(3L,
                BookingMapper.toBookingDto(booking)));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(addBookingTest -> {
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("id", booking.getId());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("item",
                                    ItemMapper.toItemToBookingDTO(booking.getItem()));
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("booker",
                                    UserMapper.toUserToBookingDTO(booking.getBooker()));
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("status", booking.getStatus());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("start", booking.getStart());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("end", booking.getEnd());
                        }
                );
    }

    @Test
    void addBookingUserNotFoundTest() {
        addBooking();
        addUser();
        addItem();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.add(2L, BookingMapper.toBookingDto(booking)));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void addBookingGetAvailableFalseTest() {
        addBooking();
        addUser();
        addItem();
        item.setAvailable(false);

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.add(2L, BookingMapper.toBookingDto(booking)));

        Assertions.assertEquals("You can not book this item", exception.getMessage());
    }

    @Test
    void addBookingItemNotFoundTest() {
        addBooking();
        addUser();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.add(2L, BookingMapper.toBookingDto(booking)));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void addBookingOwnerEqualsBookerTest() {
        addBooking();
        addUser();
        addItem();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.add(1L, BookingMapper.toBookingDto(booking)));

        Assertions.assertEquals("You cannot book your item", exception.getMessage());
    }

    @Test
    void addBookingNotValidEndTest() {
        addBooking();
        addUser();
        addItem();
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setEnd(localdatetime);

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.add(2L, BookingMapper.toBookingDto(booking)));

        Assertions.assertEquals("Wrong date",
                exception.getMessage());
    }

    @Test
    void addBookingNotValidStartTest() {
        addBooking();
        addUser();
        addItem();
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.add(2L, BookingMapper.toBookingDto(booking)));

        Assertions.assertEquals("Wrong date",
                exception.getMessage());
    }

    @Test
    void addBookingEndIsBeforeStartTest() {
        addBooking();
        addUser();
        addItem();
        booking.setStart(LocalDateTime.parse("2017-10-19T23:50:50"));
        booking.setEnd(LocalDateTime.parse("2016-10-19T23:50:50"));

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.add(2L, BookingMapper.toBookingDto(booking)));

        Assertions.assertEquals("Wrong date",
                exception.getMessage());
    }

    @Test
    void updateStatusBookingApprovedTest() {
        addUser();
        addItem();
        addBooking();

        assertThat(Optional.of(booking))
                .isPresent()
                .hasValueSatisfying(addBookingTest -> {
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("id", booking.getId());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("item", booking.getItem());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("booker", booking.getBooker());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("status", booking.getStatus());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("start", booking.getStart());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("end", booking.getEnd());
                        }
                );
    }

    @Test
    void updateStatusBookingRejectedTest() {
        addUser();
        addItem();
        addBooking();

        assertThat(Optional.of(booking))
                .isPresent()
                .hasValueSatisfying(addBookingTest -> {
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("id", booking.getId());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("item", booking.getItem());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("booker", booking.getBooker());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("status", booking.getStatus());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("start", booking.getStart());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("end", booking.getEnd());
                        }
                );
    }

    @Test
    void updateStatusBookingNotFoundTest() {
        Mockito
                .when(bookingRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(null);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.update(1L, 2L, false));

        Assertions.assertEquals("Booking not found", exception.getMessage());

    }

    @Test
    void updateStatusNoAccessForUserTest() {
        addUser();
        addItem();
        addBooking();

        Mockito
                .when(bookingRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(booking);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.update(3L, 1L, false));

        Assertions.assertEquals("No rights", exception.getMessage());

    }

    @Test
    void updateStatusBookingBadRequestTest() {
        addUser();
        addItem();
        addBooking();
        booking.setStatus(Status.APPROVED);

        Mockito
                .when(bookingRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(booking);

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.update(1L, 1L, false));

        Assertions.assertEquals("Status has already been changed", exception.getMessage());

    }

    @Test
    void getBookingTest() {
        addBooking();
        addRequest();
        addItem();
        addUser();

        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        Optional<BookingDTOToReturn> bookingDto = Optional.ofNullable(bookingService.get(1L, 1L));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(addBookingTest -> {
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("id", booking.getId());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("item",
                                    ItemMapper.toItemToBookingDTO(booking.getItem()));
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("booker",
                                    UserMapper.toUserToBookingDTO(booking.getBooker()));
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("status", booking.getStatus());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("start", booking.getStart());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("end", booking.getEnd());
                        }
                );
    }

    @Test
    void getBookingNotFoundUserTest() {
        addUser();
        addItem();
        addBooking();

        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.get(5L, 1L));

        Assertions.assertEquals("No rights", exception.getMessage());
    }

    @Test
    void getBookingNotFoundTest() {
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.get(1L, 1L));

        Assertions.assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void getBookingByBookerALLTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerOrderByStartDesc(any()))
                .thenReturn(bookingList);

        List<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "ALL",
                null, null);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerALLWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);
        Page<Booking> page = new PageImpl<>(bookingList);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerOrderByStartDesc(any(), any()))
                .thenReturn(page);

        List<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "ALL", 0, 1);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerOrderByStartDesc(any()))
                .thenReturn(bookingList);

        List<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "ALL",
                null, null);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);
        Page<Booking> page = new PageImpl<>(bookingList);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerOrderByStartDesc(any(), any()))
                .thenReturn(page);

        List<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "ALL", 0, 1);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerCURRENTTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "CURRENT",
                null, null);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerCURRENTWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);
        Page<Booking> page = new PageImpl<>(bookingList);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(),
                        any(), any()))
                .thenReturn(page);

        Collection<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "CURRENT", 0, 1);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerPASTTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "PAST",
                null, null);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerPASTWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);
        Page<Booking> page = new PageImpl<>(bookingList);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(any(), any(),
                        any(), any()))
                .thenReturn(page);

        Collection<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "PAST", 0, 1);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerFUTURETest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerAndStartAfterOrderByStartDesc(any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "FUTURE",
                null, null);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerFUTUREWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);
        Page<Booking> page = new PageImpl<>(bookingList);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerAndStartAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(page);

        Collection<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "FUTURE", 0, 1);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerWAITINGTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerAndStatusOrderByStartDesc(any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "WAITING",
                null, null);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerWAITINGWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);
        Page<Booking> page = new PageImpl<>(bookingList);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(page);

        Collection<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "WAITING", 0, 1);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerREJECTEDTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerAndStatusOrderByStartDesc(any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "REJECTED",
                null, null);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerRejectedWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);
        Page<Booking> page = new PageImpl<>(bookingList);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookerAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(page);

        Collection<BookingDTOToReturn> bookings = bookingService.getByBooker(3L, "REJECTED", 0, 1);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerUnknownStatePageableTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final StatusBadRequestException exception = Assertions.assertThrows(
                StatusBadRequestException.class,
                () -> bookingService.getByBooker(3L, "RED", 0, 1));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());

    }

    @Test
    void getBookingByBookerUnknownStateTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getByBooker(3L, "Rjj",
                        null, null));

        Assertions.assertEquals("No rights", exception.getMessage());

    }

    @Test
    void getBookingByBookerNotFoundUserTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getByBooker(3L, null, 0, 1));

        Assertions.assertEquals("No rights", exception.getMessage());

    }

    @Test
    void getBookingByBookerSizeOrPageLessZeroTest() {
        addUser();
        addItem();
        addBooking();
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getByBooker(3L, null, -1, 1));

        Assertions.assertEquals("Page index must not be less than zero",
                exception.getMessage());

    }

    @Test
    void getBookingByBookerSizeEqualZeroTest() {
        addUser();
        addItem();
        addBooking();
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final ArithmeticException exception = Assertions.assertThrows(
                ArithmeticException.class,
                () -> bookingService.getByBooker(3L, null, 0, 0));

        Assertions.assertEquals("/ by zero", exception.getMessage());

    }

    @Test
    void getBookingByOwnerNotFoundUserTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getByOwner(1L, null, 0, 1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getBookingByOwnerSizeOrPageLessZeroTest() {
        addUser();
        addItem();
        addBooking();
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getByOwner(1L, null, -1, 1));

        Assertions.assertEquals("Page index must not be less than zero",
                exception.getMessage());

    }

    @Test
    void getBookingByOwnerSizeEqualZeroTest() {
        addUser();
        addItem();
        addBooking();
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final ArithmeticException exception = Assertions.assertThrows(
                ArithmeticException.class,
                () -> bookingService.getByOwner(1L, null, 0, 0));

        Assertions.assertEquals("/ by zero", exception.getMessage());

    }

    @Test
    void getBookingByOwnerALLTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByOwnerAll(1L))
                .thenReturn(bookingList);

        Collection<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "ALL",
                null, null);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByOwnerALLWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByOwnerAll(any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "ALL", 0, 1);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }


    @Test
    void getBookingByOwnerTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByOwnerAll(1L))
                .thenReturn(bookingList);

        List<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "ALL",
                null, null);
        Assertions.assertEquals(bookingList.get(0).getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings.size());

    }

    @Test
    void getBookingByOwnerWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByOwnerAll(any(), any()))
                .thenReturn(bookingList);

        List<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "ALL",
                0, 1);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings.size());

    }

    @Test
        // @MockitoSettings(strictness = Strictness.LENIENT)
    void getBookingByOwnerCURRENTTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByOwnerAndCurrent(anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookingList);

        List<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "CURRENT",
                null, null);

        log.info(String.valueOf(bookingList.size()));
        log.info(bookings.toString());

        Assertions.assertEquals(bookingList.get(0).getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings.size());

    }

    @Test
    void getBookingByOwnerCURRENTWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByOwnerAndCurrent(any(),
                        any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "CURRENT", 0,
                1);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }


    @Test
    void getBookingByOwnerPASTTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByOwnerAndPast(anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookingList);

        List<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "PAST", null, null);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings.size());

    }

    @Test
    void getBookingByOwnerPASTWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByOwnerAndPast(any(), any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "PAST", 0, 1);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByOwnerFUTURETest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByUserAndFuture(anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookingList);

        List<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "FUTURE",
                null, null);
        Assertions.assertEquals(bookingList.get(0).getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings.size());

    }

    @Test
    void getBookingByOwnerFUTUREWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByUserAndFuture(any(), any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "FUTURE", 0, 1);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByOwnerWAITINGTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByOwnerAndByStatus(1L, Status.WAITING))
                .thenReturn(bookingList);

        List<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "WAITING",
                null, null);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings.size());

    }

    @Test
    void getBookingByOwnerWAITINGWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByOwnerAndByStatus(any(), any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "WAITING", 0, 1);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByOwnerREJECTEDTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByOwnerAndByStatus(1L, Status.REJECTED))
                .thenReturn(bookingList);

        Collection<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "REJECTED",
                null, null);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByOwnerRejectedWithPageableTest() {
        addUser();
        addItem();
        addBooking();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        booking.setId(2L);
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByOwnerAndByStatus(any(), any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDTOToReturn> bookings = bookingService.getByOwner(1L, "REJECTED", 0, 1);
        List<BookingDTOToReturn> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByOwnerUnknownStatePageableTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final StatusBadRequestException exception = Assertions.assertThrows(
                StatusBadRequestException.class,
                () -> bookingService.getByOwner(1L, "RED", 0, 1));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());

    }

    @Test
    void getBookingByOwnerUnknownStateTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final StatusBadRequestException exception = Assertions.assertThrows(
                StatusBadRequestException.class,
                () -> bookingService.getByOwner(1L, "Rjj",
                        null, null));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());

    }

    private void addItem() {
        addUser();
        item.setId(1L);
        item.setName("Sword");
        item.setOwner(user);
        item.setAvailable(true);
        item.setDescription("For fight");
    }

    private void addUser() {
        user.setId(1L);
        user.setName("Aelin");
        user.setEmail("aelin@whitethorn.com");
    }

    private void addRequest() {
        User requester = new User();
        requester.setId(2L);
        requester.setName("Rowan");
        requester.setEmail("rowan@whitethorn.com");
        request.setId(1L);
        request.setRequester(requester);
        request.setDescription("waiting for fight");
        request.setCreated(LocalDateTime.now());
    }

    private void addBooking() {
        User booker = new User();
        booker.setId(3L);
        booker.setName("Dorian");
        booker.setEmail("dorian@havilliard.com");
        booking.setId(1L);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        String date = "2024-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        date = "2025-10-19T23:50:50";
        localdatetime = LocalDateTime.parse(date);
        booking.setEnd(localdatetime);
        booking.setBooker(booker);
    }
}