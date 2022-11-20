package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "SELECT i FROM Item i WHERE i.owner.id = ?1")
    List<Item> findAllItemsByOwner(Long id);

    @Query("select i from Item i" + " where upper(i.name) like upper(concat('%', ?1, '%'))" + " or upper(i.description) like upper(concat('%', ?1, '%'))")
    Collection<Item> search(String text);
}