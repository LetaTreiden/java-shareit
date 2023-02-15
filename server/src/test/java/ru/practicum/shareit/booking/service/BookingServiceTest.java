package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDtoTwo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    BookingServiceImpl bookingService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    private final Booking booking = new Booking();
    private final Item item = new Item();
    private final User user = new User();
    private final ItemRequest request = new ItemRequest();

    @Test
    void addBookingTest() {
        addBooking();
        addRequest();
        addItem();
        addUser();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.save(any()))
                .thenReturn(booking);

        Optional<BookingDtoTwo> bookingDto = Optional.ofNullable(bookingService.addBooking(3L,
                BookingMapper.toBookingDto(booking)));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(addBookingTest -> {
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("id", booking.getId());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("item", booking.getItem());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("item", booking.getItem());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("booker", booking.getBooker());
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
                () -> bookingService.addBooking(2L, BookingMapper.toBookingDto(booking)));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
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
                () -> bookingService.addBooking(2L, BookingMapper.toBookingDto(booking)));

        Assertions.assertEquals("Невозможно забронировать данную вещь", exception.getMessage());
    }

    @Test
    void addBookingItemNotFoundTest() {
        addBooking();
        addUser();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(2L, BookingMapper.toBookingDto(booking)));

        Assertions.assertEquals("Невозможно создать запрос на бронирование", exception.getMessage());
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
                () -> bookingService.addBooking(1L, BookingMapper.toBookingDto(booking)));

        Assertions.assertEquals("Владелец не может забронировать свою вещь", exception.getMessage());
    }

    @Test
    void addBookingNotValidEndTest() {
        addBooking();
        addUser();
        addItem();
        String date = "2022-11-21T18:08:54";
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
                () -> bookingService.addBooking(2L, BookingMapper.toBookingDto(booking)));

        Assertions.assertEquals("Невозможно создать запрос на бронирование c данной датой",
                exception.getMessage());
    }

    @Test
    void addBookingNotValidStartTest() {
        addBooking();
        addUser();
        addItem();
        String date = "2020-11-21T18:08:54";
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
                () -> bookingService.addBooking(2L, BookingMapper.toBookingDto(booking)));

        Assertions.assertEquals("Невозможно создать запрос на бронирование c данной датой",
                exception.getMessage());
    }

    @Test
    void updateStatusBookingApprovedTest() {
        addUser();
        addItem();
        addBooking();

        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        Booking booking1 = booking;

        Mockito
                .when(bookingRepository.save(booking1))
                .thenReturn(booking1);


        Optional<BookingDtoTwo> bookingDto = Optional.ofNullable(bookingService
                .updateStatusBooking(booking1.getItem().getOwner().getId(),
                        booking1.getId(), true));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(addBookingTest -> {
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("id", booking.getId());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("item", booking.getItem());
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

        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        Booking booking1 = booking;

        Mockito
                .when(bookingRepository.save(booking1))
                .thenReturn(booking1);


        Optional<BookingDtoTwo> bookingDto = Optional.ofNullable(bookingService
                .updateStatusBooking(booking1.getItem().getOwner().getId(),
                        booking1.getId(), false));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(addBookingTest -> {
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("id", booking.getId());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("item", booking.getItem());
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
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatusBooking(1L, 2L, false));

        Assertions.assertEquals("Отсутсвуют данные о бронировании", exception.getMessage());

    }

    @Test
    void updateStatusNoAccessForUserTest() {
        addUser();
        addItem();
        addBooking();

        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatusBooking(3L, 1L, false));

        Assertions.assertEquals("Отсутствует доступ для пользователя", exception.getMessage());

    }

    @Test
    void updateStatusBookingBadRequestTest() {
        addUser();
        addItem();
        addBooking();
        booking.setStatus(State.APPROVED);

        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.updateStatusBooking(1L, 1L, false));

        Assertions.assertEquals("Статус бронирования уже изменен", exception.getMessage());

    }

    @Test
    void getBookingTest() {
        addUser();
        addItem();
        addBooking();

        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        Optional<BookingDtoTwo> bookingDto = Optional.ofNullable(bookingService.getBooking(1L, 1L));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(addBookingTest -> {
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("id", booking.getId());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("item", booking.getItem());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("item", booking.getItem());
                            assertThat(addBookingTest).hasFieldOrPropertyWithValue("booker", booking.getBooker());
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
                () -> bookingService.getBooking(5L, 1L));

        Assertions.assertEquals("Отсутствует доступ для пользователя", exception.getMessage());
    }

    @Test
    void getBookingNotFoundTest() {
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1L, 1L));

        Assertions.assertEquals("Запрос на бронирование не найден", exception.getMessage());
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

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByBooker(3L, "ALL",
                null, null);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByBooker(3L, "ALL", 0, 1);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByBooker(3L, "CURRENT",
                null, null);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByBooker(3L, "CURRENT", 0, 1);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByBooker(3L, "PAST",
                null, null);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByBooker(3L, "PAST", 0, 1);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByBooker(3L, "FUTURE",
                null, null);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByBooker(3L, "FUTURE", 0, 1);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByBooker(3L, "WAITING",
                null, null);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByBooker(3L, "WAITING", 0, 1);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByBooker(3L, "REJECTED",
                null, null);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByBooker(3L, "REJECTED", 0, 1);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByBookerNotFoundUserTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingByBooker(3L, null, 0, 1));

        Assertions.assertEquals("Для пользователя нет доступа", exception.getMessage());

    }

    @Test
    void getBookingByOwnerNotFoundUserTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingByOwner(1L, "ALL", 0, 1));

        Assertions.assertEquals("Для пользователя нет доступа", exception.getMessage());
    }

    @Test
    void getBookingByOwnerALLTest() {
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
                .when(bookingRepository.findByBookingForOwnerWithAll(any()))
                .thenReturn(bookingList);

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByOwner(1L, "ALL",
                null, null);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookingForOwnerWithAll(any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByOwner(1L, "ALL", 0, 1);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByOwnerCURRENTTest() {
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
                .when(bookingRepository.findByBookingForOwnerWithCurrent(any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByOwner(1L, "CURRENT",
                null, null);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());
    }

    @Test
    void getBookingByOwnerCURRENTWithPageableTest() {
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
                .when(bookingRepository.findByBookingForOwnerWithCurrent(any(),
                        any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByOwner(1L, "CURRENT", 0,
                1);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookingForOwnerWithPast(any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByOwner(1L, "PAST",
                null, null);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByOwnerPASTWithPageableTest() {
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
                .when(bookingRepository.findByBookingForOwnerWithPast(any(), any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByOwner(1L, "PAST", 0, 1);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookingForOwnerWithFuture(any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByOwner(1L, "FUTURE",
                null, null);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByOwnerFUTUREWithPageableTest() {
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
                .when(bookingRepository.findByBookingForOwnerWithFuture(any(), any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByOwner(1L, "FUTURE", 0, 1);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookingForOwnerWithWaitingOrRejected(any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByOwner(1L, "WAITING",
                null, null);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }

    @Test
    void getBookingByOwnerWAITINGWithPageableTest() {
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
                .when(bookingRepository.findByBookingForOwnerWithWaitingOrRejected(any(), any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByOwner(1L, "WAITING", 0, 1);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookingForOwnerWithWaitingOrRejected(any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByOwner(1L, "REJECTED",
                null, null);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

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
        String date = "2021-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        bookingList.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.findByBookingForOwnerWithWaitingOrRejected(any(), any(), any()))
                .thenReturn(bookingList);

        Collection<BookingDtoTwo> bookings = bookingService.getBookingByOwner(1L, "REJECTED", 0, 1);
        List<BookingDtoTwo> bookings1 = List.copyOf(bookings);

        Assertions.assertEquals(bookingList.get(0).getId(), bookings1.get(0).getId());
        Assertions.assertEquals(bookingList.size(), bookings1.size());

    }


    private void addItem() {
        addUser();
        item.setId(1L);
        item.setName("Fork");
        item.setOwner(user);
        item.setAvailable(true);
        item.setDescription("Designed for food");
    }

    private void addUser() {
        user.setId(1L);
        user.setName("Buffy");
        user.setEmail("buffy@vampire.com");
    }

    private void addRequest() {
        User requestor = new User();
        requestor.setId(2L);
        requestor.setName("Leo");
        requestor.setEmail("leo@angel.com");
        request.setId(1L);
        request.setRequestor(requestor);
        request.setDescription("I need a fork to eat");
        request.setCreated(LocalDateTime.now());
    }

    private void addBooking() {
        User booker = new User();
        booker.setId(3L);
        booker.setName("Katya");
        booker.setEmail("katya@katya.com");
        booking.setId(1L);
        booking.setItem(item);
        booking.setStatus(State.WAITING);
        String date = "2023-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setStart(localdatetime);
        date = "2023-11-26T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        booking.setEnd(localdatetime);
        booking.setBooker(booker);
    }

}