package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM items AS i " +
            "WHERE ((LOWER(name) iLike CONCAT('%', LOWER(?1), '%')) " +
            "OR (LOWER(description) Like CONCAT('%', LOWER(?1), '%'))) " +
            "AND (available)")
    List<Item> findItemsByNameOrDescription(String substring);

    @Query(nativeQuery = true, value = "SELECT * FROM items " +
            "WHERE ((LOWER(name) Like CONCAT('%', LOWER(?1), '%')) " +
            "OR (LOWER(description) Like CONCAT('%', LOWER(?1), '%'))) " +
            "AND (available)")
    Page<Item> findItemsByNameOrDescription(String substring, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM items AS i WHERE i.REQUEST_ID = ?1")
    List<Item> findByRequest(Long idRequest);

    List<Item> findByOwner(User user);

    Page<Item> findByOwner(User user, Pageable pageable);

    List<Item> findAllByRequestIdInAndAvailableTrue(List<ItemRequest> items);

}