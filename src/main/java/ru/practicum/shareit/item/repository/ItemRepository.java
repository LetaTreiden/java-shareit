package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // @Query("SELECT i FROM Item i WHERE i.owner.id = ?1")
    Collection<Item> findAllItemsByOwner(Long id);
}