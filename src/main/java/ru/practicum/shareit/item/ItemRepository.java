package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner(User user);

    @Query(nativeQuery = true, value = "SELECT * FROM items AS i " +
            "WHERE ((LOWER(name) iLike CONCAT('%', LOWER(?1), '%')) " +
            "OR (LOWER(description) Like CONCAT('%', LOWER(?1), '%'))) " +
            "AND (available)")
    List<Item> findItemsByNameOrDescription(String substring);


}