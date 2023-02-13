package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusBadRequestException;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOWithBookings;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDTOWithItems;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestExecutionListeners({DirtiesContextBeforeModesTestExecutionListener.class})
@Slf4j
class ShareItTests {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final RequestService requestService;

    private final UserService userService;

    @Test
    void contextLoads() {
    }

    @Test
    void createUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setName("Aelin");
        userDTO.setEmail("aelin@whitethorn.com");

        UserDTO user = userService.create(userDTO);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getName()).isEqualTo("Aelin");
        assertThat(user.getEmail()).isEqualTo("aelin@whitethorn.com");
    }

    @Test
    void getItem() {
        ItemDTOWithBookings itemDTO = itemService.get(1L, 1L);
        assertThat(itemDTO).isNotNull();
        assertThat(itemDTO.getId()).isEqualTo(1L);
        assertThat(itemDTO.getName()).isEqualTo("Sword");
        assertThat(itemDTO.getDescription()).isEqualTo("For fights");
        assertThat(itemDTO.getAvailable()).isEqualTo(true);
    }

    @Test
    void getBookingWithEndBeforeStart() {
        BookingDTO bDto = new BookingDTO();
        bDto.setBookerId(1L);
        bDto.setId(1);
        bDto.setStart(LocalDateTime.of(2023, Month.FEBRUARY, 13, 12, 29, 00));
        bDto.setEnd(LocalDateTime.of(2022, Month.FEBRUARY, 13, 12, 29, 00));
        bDto.setStatus(Status.WAITING);
        bDto.setItemId(3L);

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.add(1L, bDto));
        assertThat(exception.getMessage()).isEqualTo("Wrong date");
    }

    @Test
    void bookingGet() {
        BookingDTOToReturn booking = bookingService.get(2L, 4L);
        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isEqualTo(4L);
        assertThat(booking.getBooker().getId()).isEqualTo(1);
        assertThat(booking.getStatus()).isEqualTo(Status.WAITING);
        assertThat(booking.getStart()).isEqualTo(LocalDateTime.parse("2023-11-11T12:32:59"));
    }

    @Test
    void bookingGetNoRights() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.get(2L, 1L));
        assertThat(exception.getMessage()).isEqualTo("No rights");
    }

    @Test
    void getAllOwnItemTest() {
        List<ItemDTOWithBookings> items = itemService.getAllByOwner(1L, null, null);
        assertThat(items).isNotEmpty();
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0).getName()).isEqualTo("Knives");
    }

    @Test
    void getAllOwnItemNotFoundUserTest() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getAllByOwner(5L, null, null));
        assertThat(exception.getMessage()).isEqualTo("User not found");

    }

    @Test
    void getAllOwnItemFromOrSizeLessThanZeroTest() {
        final ArithmeticException exception = Assertions.assertThrows(
                ArithmeticException.class,
                () -> itemService.getAllByOwner(1L, -1, 0));

        Assertions.assertEquals("/ by zero",
                exception.getMessage());
    }

    @Test
    void getAllOwnItemSizeEqualToZeroTest() {
        final ArithmeticException exception = Assertions.assertThrows(
                ArithmeticException.class,
                () -> itemService.getAllByOwner(1L, 1, 0));

        Assertions.assertEquals("/ by zero", exception.getMessage());
    }

    @Test
    void getItemsForRentTest() {
        List<ItemDTO> items = itemService.getForRent("Sw", null, null);
        assertThat(items).isNotEmpty();
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0).getName()).isEqualTo("Sword");
        items = itemService.getForRent("fight", 0, 1);
        assertThat(items).isNotEmpty();
        assertThat(items.size()).isEqualTo(1);
    }

    @Test
    void getItemForRentEqualToZeroTest() {
        final ArithmeticException exception = Assertions.assertThrows(
                ArithmeticException.class,
                () -> itemService.getForRent("S", 0, 0));

        Assertions.assertEquals("/ by zero", exception.getMessage());

    }

    @Test
    void getItemsForRentFromOrSizeLessThanZeroTest() {
        final ArithmeticException exception = Assertions.assertThrows(
                ArithmeticException.class,
                () -> itemService.getForRent("F", -1, 0));

        Assertions.assertEquals("/ by zero",
                exception.getMessage());

    }

    @Test
    void getBookingByBookerStateAllTest() {
        List<BookingDTOToReturn> bookings = bookingService.getByBooker(3L,
                "ALL", null, null);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(2);
        bookings = bookingService.getByBooker(3L, "ALL", null, null);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(2);
    }

    @Test
    void getBookingByBookerStateCurrentTest() {
        List<BookingDTOToReturn> bookings = bookingService.getByBooker(3L,
                "CURRENT", null, null);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(2);
    }

    @Test
    void getBookingByBookerStatePastTest() {
        List<BookingDTOToReturn> bookings = bookingService.getByBooker(3L,
                "PAST", null, null);
        assertThat(bookings).isEmpty();
    }

    @Test
    void getBookingByBookerStateFutureTest() {
        List<BookingDTOToReturn> bookings = bookingService.getByBooker(2L,
                "FUTURE", null, null);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(3);
    }

    @Test
    void getBookingByBookerStateWaitingTest() {
        List<BookingDTOToReturn> bookings = bookingService.getByBooker(1L,
                "WAITING", 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(4);
    }

    @Test
    void getBookingsByBookerUnknownStatePageableTest() {
        final StatusBadRequestException exception = Assertions.assertThrows(
                StatusBadRequestException.class,
                () -> bookingService.getByBooker(3L, "WRONG", 0, 1));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());

    }

    @Test
    void getBookingsByBookerNotFoundUserTest() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getByBooker(5L, null, 0, 1));

        Assertions.assertEquals("No rights", exception.getMessage());

    }

    @Test
    void getBookingsByBookerSizeOrPageLessZeroTest() {
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getByBooker(3L, null, -1, 1));

        Assertions.assertEquals("Page index must not be less than zero",
                exception.getMessage());

    }

    @Test
    void getBookingsByBookerSizeEqualZeroTest() {
        final ArithmeticException exception = Assertions.assertThrows(
                ArithmeticException.class,
                () -> bookingService.getByBooker(3L, null, 0, 0));

        Assertions.assertEquals("/ by zero", exception.getMessage());

    }

    @Test
    void getBookingsByOwnerNotFoundUserTest() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getByOwner(8L, null, 0, 1));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getBookingsByOwnerSizeOrPageLessZeroTest() {
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getByOwner(1L, null, -1, 1));

        Assertions.assertEquals("Page index must not be less than zero",
                exception.getMessage());

    }

    @Test
    void getBookingsByOwnerByStateAllTest() {
        List<BookingDTOToReturn> bookings = bookingService.getByOwner(1L,
                "ALL", 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(2);
        bookings = bookingService.getByOwner(1L, "ALL", 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(2);
    }

    @Test
    void getBookingsByOwnerByStatePastTest() {
        List<BookingDTOToReturn> bookings = bookingService.getByOwner(3L,
                "PAST", 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(1);
    }

    @Test
    void getBookingsByOwnerByStateCurrentTest() {
        List<BookingDTOToReturn> bookings = bookingService.getByOwner(1L,
                "CURRENT", 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(2);
    }

    @Test
    void getBookingsByOwnerByStateFutureTest() {
        List<BookingDTOToReturn> bookings = bookingService.getByOwner(2L,
                "FUTURE", 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(4);
    }

    @Test
    void getBookingsByOwnerByStateWaitingOrRejectedTest() {
        List<BookingDTOToReturn> bookings = bookingService.getByOwner(2L,
                "WAITING", 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(4);
    }

    @Test
    void getAllOwnRequestTest() {
        List<RequestDTOWithItems> requests = requestService.findAllByOwner(3L);
        assertThat(requests).isNotEmpty();
        assertThat(requests.size()).isEqualTo(1);
        assertThat(requests.get(0).getId()).isEqualTo(3);
    }

    @Test
    void findAllOwnRequestsNotFoundUserTest() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.findAllByOwner(5L));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void findAllRequestsTest() {
        List<RequestDTOWithItems> requests = requestService.findAll(1L, 0, 1);
        assertThat(requests).isNotEmpty();
        assertThat(requests.size()).isEqualTo(1);
        assertThat(requests.get(0).getId()).isEqualTo(2);
    }

    @Test
    void findAllRequestsSizeOrPageLessZeroTest() {
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> requestService.findAll(1L, 1, -1));

        Assertions.assertEquals("Page size must not be less than one",
                exception.getMessage());
    }

    @Test
    void findAllRequestsSizeEqualZeroTest() {
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> requestService.findAll(1L, 1, 0));

    }


}
