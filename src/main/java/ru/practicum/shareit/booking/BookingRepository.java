package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemOrderByStartDesc(Item item);

    List<Booking> findByBookerAndStatusOrderByStartDesc(User booker, State status);

    List<Booking> findByBookerOrderByStartDesc(User booker);

    List<Booking> findByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime now);

    List<Booking> findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime s, LocalDateTime e);

    List<Booking> findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(User booker, LocalDateTime s,
                                                                         LocalDateTime e);

    List<Booking> findByItemAndBookerAndStartBeforeAndEndBefore(Item item, User booker, LocalDateTime s,
                                                                LocalDateTime e);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.END_DATE < ?2 AND b.START_DATE < ?3 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithPast(Long i, LocalDateTime e, LocalDateTime s);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.START_DATE < ?2 AND b.END_DATE > ?3 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithCurrent(Long i, LocalDateTime s, LocalDateTime e);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.START_DATE > ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithFuture(Long i, LocalDateTime s);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithAll(Long i);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.STATUS LIKE ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithWaitingOrRejected(Long i, String status);

}
