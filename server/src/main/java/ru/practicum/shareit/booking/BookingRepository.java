package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Booking> findByBookerAndStatusOrderByStartDesc(User booker, State status, Pageable pageable);

    List<Booking> findByBookerOrderByStartDesc(User booker);

    Page<Booking> findByBookerOrderByStartDesc(User booker, Pageable pageable);

    List<Booking> findByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime now);

    Page<Booking> findByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime s, LocalDateTime e);

    Page<Booking> findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime s, LocalDateTime e,
                                                                        Pageable pageable);

    List<Booking> findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(User booker, LocalDateTime s,
                                                                         LocalDateTime e);

    Page<Booking> findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(User booker, LocalDateTime s,
                                                                         LocalDateTime e, Pageable pageable);

    List<Booking> findByItemAndBookerAndStartBeforeAndEndBefore(Item item, User booker, LocalDateTime now,
                                                                LocalDateTime e);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.END_DATE < ?2 AND b.START_DATE < ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithPast(Long i, LocalDateTime now);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.END_DATE < ?2 AND b.START_DATE < ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithPast(Long i, LocalDateTime now, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.START_DATE < ?2 AND b.END_DATE > ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithCurrent(Long i, LocalDateTime now);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.START_DATE < ?2 AND b.END_DATE > ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithCurrent(Long i, LocalDateTime now, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.START_DATE > ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithFuture(Long i, LocalDateTime s);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.START_DATE > ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithFuture(Long i, LocalDateTime s, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithAll(Long i);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithAll(Long i, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.STATUS LIKE ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithWaitingOrRejected(Long i, String status);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.STATUS LIKE ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByBookingForOwnerWithWaitingOrRejected(Long i, String status, Pageable pageable);

}
