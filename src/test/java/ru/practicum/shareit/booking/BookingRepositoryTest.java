package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
class BookingRepositoryTest {

    private final User user = new User();
    private final Item item = new Item();
    private final ItemRequest request = new ItemRequest();
    private final Booking bookingOne = new Booking();
    private final Booking bookingTwo = new Booking();
    private final List<Booking> bookingsList = new ArrayList<>();
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findByItemOrderByStartDescTest() {
        addItem();
        addBookingOne();
        addUser();
        addBookingTwo();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);
        booking = em.persist(bookingTwo);
        bookingsList.add(booking);

        List<Booking> bookings = bookingRepository.findByItemOrderByStartDesc(item);
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void findByBookerAndStatusOrderByStartDescTest() {
        addItem();
        addBookingOne();
        addUser();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);

        List<Booking> bookings = bookingRepository.findByBookerAndStatusOrderByStartDesc(bookingOne.getBooker(),
                Status.WAITING);
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void testFindByBookerAndStatusOrderByStartDesc() {
        addItem();
        addBookingOne();
        addUser();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);
        Pageable pageable = PageRequest.of(0, 1);

        Page<Booking> bookingsPage = bookingRepository.findByBookerAndStatusOrderByStartDesc(bookingOne.getBooker(),
                Status.WAITING, pageable);
        List<Booking> bookings = bookingsPage.getContent();
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void findByBookerOrderByStartDescTest() {
        addItem();
        addBookingOne();
        addUser();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);

        List<Booking> bookings = bookingRepository.findByBookerOrderByStartDesc(bookingOne.getBooker());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void testFindByBookerOrderByStartDesc() {
        addItem();
        addBookingOne();
        addUser();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);
        Pageable pageable = PageRequest.of(0, 1);

        Page<Booking> bookingsPage = bookingRepository.findByBookerOrderByStartDesc(bookingOne.getBooker(),
                pageable);
        List<Booking> bookings = bookingsPage.getContent();
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void findByBookerAndStartAfterOrderByStartDesc() {
        addItem();
        addBookingOne();
        addUser();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);

        List<Booking> bookings = bookingRepository.findByBookerAndStartAfterOrderByStartDesc(bookingOne.getBooker(),
                LocalDateTime.now());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void testFindByBookerAndStartAfterOrderByStartDesc() {
        addItem();
        addBookingOne();
        addUser();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);
        Pageable pageable = PageRequest.of(0, 1);

        Page<Booking> bookingsPage = bookingRepository.findByBookerAndStartAfterOrderByStartDesc(bookingOne.getBooker(),
                LocalDateTime.now(), pageable);
        List<Booking> bookings = bookingsPage.getContent();
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void findByBookerAndStartBeforeAndEndAfterOrderByStartDescTest() {
        addItem();
        addBookingOne();
        String date = "2021-11-21T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        bookingOne.setStart(localdatetime);
        addUser();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);

        List<Booking> bookings = bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                bookingOne.getBooker(), LocalDateTime.now(), LocalDateTime.now());
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void testFindByBookerAndStartBeforeAndEndAfterOrderByStartDesc() {
        addItem();
        addBookingOne();
        String date = "2021-11-21T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        bookingOne.setStart(localdatetime);
        addUser();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);

        Pageable pageable = PageRequest.of(0, 1);

        Page<Booking> bookingsPage = bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                bookingOne.getBooker(), LocalDateTime.now(), LocalDateTime.now(), pageable);

        List<Booking> bookings = bookingsPage.getContent();
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void findByBookerAndStartBeforeAndEndBeforeOrderByStartDescTest() {
        addItem();
        addBookingOne();
        String date = "2021-11-21T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        bookingOne.setStart(localdatetime);
        date = "2021-11-23T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        bookingOne.setEnd(localdatetime);
        addUser();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);

        List<Booking> bookings = bookingRepository.findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(
                bookingOne.getBooker(), LocalDateTime.now(), LocalDateTime.now());
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void testFindByBookerAndStartBeforeAndEndBeforeOrderByStartDesc() {
        addItem();
        addBookingOne();
        String date = "2021-11-21T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        bookingOne.setStart(localdatetime);
        date = "2021-11-23T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        bookingOne.setEnd(localdatetime);
        addUser();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);

        Pageable pageable = PageRequest.of(0, 1);

        Page<Booking> bookingsPage = bookingRepository.findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(
                bookingOne.getBooker(), LocalDateTime.now(), LocalDateTime.now(), pageable);

        List<Booking> bookings = bookingsPage.getContent();
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void findByItemAndBookerAndStartBeforeAndEndBeforeTest() {
        addItem();
        addBookingOne();
        String date = "2021-11-21T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        bookingOne.setStart(localdatetime);
        date = "2021-11-23T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        bookingOne.setEnd(localdatetime);
        addUser();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);

        List<Booking> bookings = bookingRepository.findByItemAndBookerAndStartBeforeAndEndBefore(item,
                bookingOne.getBooker(), LocalDateTime.now(), LocalDateTime.now());
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void findByBookingForOwnerWithPastTest() {
        addUser();
        addItemWithoutId();
        em.persist(item);
        String date = "2021-11-21T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        addBookingOne();
        bookingOne.setStart(localdatetime);
        date = "2021-11-25T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        bookingOne.setEnd(localdatetime);
        addBookingTwo();
        date = "2022-10-25T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        bookingTwo.setStart(localdatetime);
        date = "2022-10-27T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        bookingTwo.setEnd(localdatetime);
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);
        booking = em.persist(bookingTwo);
        bookingsList.add(booking);

        List<Booking> bookings = bookingRepository.findByOwnerAndPast(1L, LocalDateTime.now());
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(1).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void findByBookingForOwnerWithPastWithPageableTest() {
        Pageable pageable = PageRequest.of(0, 2);
        addUser();
        addItemWithoutId();
        em.persist(item);
        String date = "2021-11-21T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        addBookingOne();
        bookingOne.setStart(localdatetime);
        date = "2021-11-25T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        bookingOne.setEnd(localdatetime);
        addBookingTwo();
        date = "2022-10-25T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        bookingTwo.setStart(localdatetime);
        date = "2022-10-27T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        bookingTwo.setEnd(localdatetime);
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);
        booking = em.persist(bookingTwo);
        bookingsList.add(booking);

        List<Booking> bookings = bookingRepository.findByOwnerAndPast(1L, LocalDateTime.now(), pageable);
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(1).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void findByBookingForOwnerWithFutureTest() {
        addUser();
        addItemWithoutId();
        em.persist(item);
        addBookingOne();
        addBookingTwo();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);
        booking = em.persist(bookingTwo);
        bookingsList.add(0, booking);

        List<Booking> bookings = bookingRepository.findByUserAndFuture(1L, LocalDateTime.now());
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void findByBookingForOwnerWithFutureWithPageableTest() {
        Pageable pageable = PageRequest.of(0, 2);
        addUser();
        addItemWithoutId();
        em.persist(item);
        addBookingOne();
        addBookingTwo();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);
        booking = em.persist(bookingTwo);
        bookingsList.add(booking);

        List<Booking> bookings = bookingRepository.findByUserAndFuture(1L, LocalDateTime.now(), pageable);
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void findByBookingForOwnerWithAllTest() {
        addUser();
        addItemWithoutId();
        em.persist(item);
        addBookingOne();
        addBookingTwo();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);
        booking = em.persist(bookingTwo);
        bookingsList.add(0, booking);

        List<Booking> bookings = bookingRepository.findByOwnerAll(1L);
        log.info(bookings.toString());
        log.info(bookingsList.toString());
        assertThat(bookingsList.get(0)).isEqualTo(bookings.get(0));
    }

    @Test
    void findByBookingForOwnerWithAllWithPageableTest() {
        Pageable pageable = PageRequest.of(0, 2);
        addUser();
        addItemWithoutId();
        em.persist(item);
        addBookingOne();
        addBookingTwo();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);
        booking = em.persist(bookingTwo);
        bookingsList.add(booking);

        List<Booking> bookings = bookingRepository.findByOwnerAll(1L, pageable);
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void findByBookingForOwnerWithWaitingOrRejectedTest() {
        addUser();
        addItemWithoutId();
        em.persist(item);
        addBookingOne();
        addBookingTwo();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);
        booking = em.persist(bookingTwo);
        bookingsList.add(0, booking);

        List<Booking> bookings = bookingRepository.findByOwnerAndByStatus(1L, Status.WAITING);
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    @Test
    void findByBookingForOwnerWithWaitingOrRejectedWithPageableTest() {
        Pageable pageable = PageRequest.of(0, 2);
        addUser();
        addItemWithoutId();
        em.persist(item);
        addBookingOne();
        addBookingTwo();
        Booking booking = em.persist(bookingOne);
        bookingsList.add(booking);
        booking = em.persist(bookingTwo);
        bookingsList.add(booking);

        List<Booking> bookings = bookingRepository.findByOwnerAndByStatus(1L, "WAITING",
                pageable);
        assertThat(bookingsList.size()).isEqualTo(bookings.size());
        assertThat(bookingsList.get(0).getId()).isEqualTo(bookings.get(0).getId());
    }

    private void addItem() {
        addRequest();
        item.setId(1);
        item.setName("Sword");
        item.setOwner(user);
        item.setAvailable(true);
        item.setDescription("For fights");
        item.setRequestId(request);
    }

    private void addItemWithoutId() {
        addRequest();
        item.setName("Sword");
        item.setOwner(user);
        item.setAvailable(true);
        item.setDescription("For fights");
        item.setRequestId(request);
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

    private void addBookingOne() {
        User user1 = new User();
        user1.setId(2L);
        user1.setName("Rowan");
        user1.setEmail("rowan@whitethorn.com");
        bookingOne.setBooker(user1);
        bookingOne.setItem(item);
        String date = "2024-11-20T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        bookingOne.setStart(localdatetime);
        date = "2024-11-25T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        bookingOne.setEnd(localdatetime);
        bookingOne.setStatus(Status.WAITING);
    }

    private void addBookingTwo() {
        User booker = new User();
        booker.setId(3L);
        booker.setName("Dorin");
        booker.setEmail("dorian@havilliard.com");
        bookingTwo.setStatus(Status.WAITING);
        String date = "2023-11-21T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        bookingTwo.setStart(localdatetime);
        date = "2023-11-22T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        bookingTwo.setEnd(localdatetime);
        bookingTwo.setBooker(booker);
        bookingTwo.setItem(item);
    }

    @AfterEach
    private void delete() {
        bookingRepository.deleteAll();
        bookingsList.clear();
    }

}