package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //переделать все запросы с натив куери

    List<Booking> findByBookerAndStatusOrderByStartDesc(User booker, Status status);

    List<Booking> findByBookerOrderByStartDesc(User booker);

    Page<Booking> findByBookerAndStatusOrderByStartDesc(User booker, Status status, Pageable pageable);

    Page<Booking> findByBookerOrderByStartDesc(User booker, Pageable pageable);

    List<Booking> findByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime now);

    Page<Booking> findByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime s, LocalDateTime e);

    Page<Booking> findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime s, LocalDateTime e,
                                                                        Pageable pageable);

    List<Booking> findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(User booker, LocalDateTime s, LocalDateTime e);

    Page<Booking> findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(User booker, LocalDateTime s,
                                                                         LocalDateTime e, Pageable pageable);

    List<Booking> findByItemAndBookerAndStartBeforeAndEndBefore(Item item, User booker, LocalDateTime s,
                                                                LocalDateTime e);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN" + " (SELECT i.id FROM Item i WHERE i.owner.id = ?1)"
            + " ORDER BY b.id DESC")
    List<Booking> findByOwnerAll(long userId);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByOwnerAll(Long i, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN "
            + "(SELECT i.id FROM Item i WHERE i.owner.id = ?1) AND b.start < ?2 AND b.end > ?2"
            + " ORDER BY b.id DESC")
    List<Booking> findByOwnerAndCurrent(long userId, LocalDateTime currentDate);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.START_DATE < ?2 AND b.END_DATE > ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByOwnerAndCurrent(Long i, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN "
            + "(SELECT i.id FROM Item i WHERE i.owner.id = ?1) AND b.end < ?2"
            + " ORDER BY b.id DESC")
    List<Booking> findByOwnerAndPast(long userId, LocalDateTime currentDate);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.END_DATE < ?2 AND b.START_DATE < ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByOwnerAndPast(Long i, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN "
            + "(SELECT i.id FROM Item i WHERE i.owner.id = ?1) AND b.start > ?2" + " ORDER BY b.id DESC")
    List<Booking> findByUserAndFuture(long userId, LocalDateTime currentDate);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.START_DATE > ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByUserAndFuture(Long i, LocalDateTime s, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN "
            + "(SELECT i.id FROM Item i WHERE i.owner.id = ?1) AND b.status = ?2" + " ORDER BY b.id DESC")
    List<Booking> findByOwnerAndByStatus(long userId, Status status);

    @Query(nativeQuery = true, value = "SELECT * FROM BOOKINGS as b " +
            "LEFT JOIN ITEMS as i ON b.ITEM_ID = i.ID " +
            "WHERE i.OWNER_ID = ?1 AND b.STATUS LIKE ?2 " +
            "ORDER BY b.START_DATE DESC")
    List<Booking> findByOwnerAndByStatus(Long i, String status, Pageable pageable);

    Booking findFirstByStatusAndItemAndStartIsAfter(Status state, Item item, LocalDateTime time, Sort sort);

    Booking findFirstByStatusAndItemAndStartLessThanEqual(Status state, Item item, LocalDateTime time, Sort sort);

    @Query("select b " +
            "from Booking b " +
            "where b.item in ?1 " +
            " and b.status = 'APPROVED'")
    List<Booking> findApprovedForItems(Collection<Item> items, Sort sort);

    List<Booking> findByItemOrderByStartDesc(Item item);
}
