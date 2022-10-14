package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "SELECT i FROM Item i WHERE i.owner.id = ?1")
    Collection<? extends ItemDTO> findAllItemsByOwner(Long id);

    @Query("select i from Item i" + " where upper(i.name) like upper(concat('%', ?1, '%'))" + " or upper(i.description) like upper(concat('%', ?1, '%'))")
    Collection<Item> search(String text);
}