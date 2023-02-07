package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBookings;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM items AS i " +
            "WHERE ((LOWER(name) iLike CONCAT('%', LOWER(?1), '%')) " +
            "OR (LOWER(description) Like CONCAT('%', LOWER(?1), '%'))) " +
            "AND (available)")
    List<Item> findItemsByNameOrDescription(String substring);

    @Query(value = "select " +
            "i.id, " +
            "i.name, " +
            "i.description, " +
            "i.available, " +
            "(select id from bookings " +
            "where item_id = i.id and i.owner_id = ?2 and start_date <= ?1 " +
            "order by start_date desc limit 1) \"lastBookingId\", " +
            "(select booker_id from bookings " +
            "where item_id = i.id and i.owner_id = ?2 and start_date <= ?1 " +
            "order by start_date desc limit 1) \"lastBookerId\", " +
            "(select id from bookings " +
            "where item_id = i.id and i.owner_id = ?2 and start_date > ?1 " +
            "order by start_date limit 1) \"nextBookingId\", " +
            "(select booker_id from bookings " +
            "where item_id = i.id and i.owner_id = ?2 and start_date > ?1 " +
            "order by start_date limit 1) \"nextBookerId\" " +
            "from items i" + " where i.owner_id = ?2 order by id", nativeQuery = true)
    List<ItemWithBookings> findAllByOwnerWithBookings(LocalDateTime date, Long ownerId);
}