package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1")
    List<Booking> findBookingsByBookerId(Long id);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND" +
            " b.start < CURRENT_TIMESTAMP AND b.end > CURRENT_TIMESTAMP")
    List<Booking> findBookingsByBookerIdWithCurrentStatus(Long id);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND" +
            " b.start < CURRENT_TIMESTAMP AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findBookingsByBookerIdWithPastStatus(Long id);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND" + " b.start > CURRENT_TIMESTAMP ")
    List<Booking> findBookingsByBookerIdWithFutureStatus(Long id);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND  b.status = ?2")
    List<Booking> findBookingsByBookerIdWithWaitingOrRejectStatus(Long id, BookingStatus status);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.id = ?1")
    List<Booking> findAllItemBookings(Long id);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.start < CURRENT_TIMESTAMP ")
    List<Booking> findAllItemBookingsPast(Long id);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.start > CURRENT_TIMESTAMP ")
    List<Booking> findAllItemBookingsFuture(Long id);

    List<Booking> findAllByItemOwner(User owner);

    List<Booking> findAllByItemOwnerAndStatus(User owner, BookingStatus status);

    List<Booking> findAllByItemOwnerAndStartIsBeforeAndEndIsAfter(User owner, LocalDateTime nowStart,
                                                                  LocalDateTime nowEnd);

    List<Booking> findAllByItemOwnerAndEndIsBefore(User owner, LocalDateTime now);

    List<Booking> findAllByItemOwnerAndStartIsAfter(User owner, LocalDateTime now);
}