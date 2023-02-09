package ru.practicum.shareit.booking;

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

    List<Booking> findByBookerAndStatusOrderByStartDesc(User booker, Status status);

    List<Booking> findByBookerOrderByStartDesc(User booker);

    List<Booking> findByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime now);

    List<Booking> findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime s, LocalDateTime e);

    List<Booking> findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(User booker, LocalDateTime s, LocalDateTime e);

    List<Booking> findByItemAndBookerAndStartBeforeAndEndBefore(Item item, User booker, LocalDateTime s,
                                                                LocalDateTime e);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN" + " (SELECT i.id FROM Item i WHERE i.owner.id = ?1)"
            + " ORDER BY b.id DESC")
    List<Booking> findByOwnerAll(long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN "
            + "(SELECT i.id FROM Item i WHERE i.owner.id = ?1) AND b.start < ?2 AND b.end > ?2"
            + " ORDER BY b.id DESC")
    List<Booking> findByOwnerAndCurrent(long userId, LocalDateTime currentDate);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN "
            + "(SELECT i.id FROM Item i WHERE i.owner.id = ?1) AND b.end < ?2"
            + " ORDER BY b.id DESC")
    List<Booking> findByOwnerAndPast(long userId, LocalDateTime currentDate);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN "
            + "(SELECT i.id FROM Item i WHERE i.owner.id = ?1) AND b.start > ?2" + " ORDER BY b.id DESC")
    List<Booking> findByUserAndFuture(long userId, LocalDateTime currentDate);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN "
            + "(SELECT i.id FROM Item i WHERE i.owner.id = ?1) AND b.status = ?2" + " ORDER BY b.id DESC")
    List<Booking> findByOwnerAndByStatus(long userId, Status status);

    Booking findFirstByStatusAndItemAndStartIsAfter(Status state, Item item, LocalDateTime time, Sort sort);

    Booking findLastByStatusAndItemAndEndIsBefore(Status state, Item item, LocalDateTime time, Sort sort);

    @Query("select b " +
            "from Booking b " +
            "where b.item in ?1 " +
            " and b.status = 'APPROVED'")
    List<Booking> findApprovedForItems(Collection<Item> items, Sort sort);
}
