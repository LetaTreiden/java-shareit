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
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusBadRequestException;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOWithBookings;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDTOWithItems;
import ru.practicum.shareit.request.service.RequestService;

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

    @Test
    void contextLoads() {
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
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.getAllByOwner(1L, -1, 0));

        Assertions.assertEquals("From or size is less than 0",
                exception.getMessage());
    }

    @Test
    void getAllOwnItemSizeEqualToZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.getAllByOwner(1L, 1, 0));

        Assertions.assertEquals("Size equals 0", exception.getMessage());
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
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.getForRent("F", 0, 0));

        Assertions.assertEquals("Size equals 0", exception.getMessage());

    }

    @Test
    void getItemsForRentFromOrSizeLessThanZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.getForRent("F", -1, 0));

        Assertions.assertEquals("From or size is less than 0",
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
    void getBookingByBookerStateFutureTest() {
        List<BookingDTOToReturn> bookings = bookingService.getByBooker(2L,
                "FUTURE", null, null);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(3);
    }

    @Test
    void getBookingByBookerStateWaitingOrRejectedTest() {
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
                () -> bookingService.getByBooker(3L, "RED", 0, 1));

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
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.getByBooker(3L, null, -1, 1));

        Assertions.assertEquals("Wrong meaning page or size",
                exception.getMessage());

    }

    @Test
    void getBookingsByBookerSizeEqualZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.getByBooker(3L, null, 0, 0));

        Assertions.assertEquals("Size equals 0!", exception.getMessage());

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
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.getByOwner(1L, null, -1, 1));

        Assertions.assertEquals("From or size is less than 0",
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
        List<RequestDTOWithItems> requests = requestService.findAll(1L, null, null);
        assertThat(requests).isNotEmpty();
        assertThat(requests.size()).isEqualTo(4);
        assertThat(requests.get(0).getId()).isEqualTo(1);
    }

    @Test
    void findAllRequestsSizeOrPageLessZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> requestService.findAll(1L, 1, -1));

        Assertions.assertEquals("From and size cannot be less than 0",
                exception.getMessage());
    }

    @Test
    void findAllRequestsSizeEqualZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> requestService.findAll(1L, 1, 0));

        Assertions.assertEquals("Size cannot be 0",
                exception.getMessage());

    }
}
