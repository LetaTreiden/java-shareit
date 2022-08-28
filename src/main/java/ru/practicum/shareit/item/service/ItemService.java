package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDTO;

import java.util.Collection;

@Repository
public interface ItemService {
    ItemDTO createItem(Long userId, ItemDTO itemDto) throws ValidationException, NotFoundException;

    ItemDTO findById(Long itemId) throws NotFoundException;

    Collection<ItemDTO> findByUser(Long userId) throws NotFoundException;

    ItemDTO update(Long userId, Long itemId, ItemDTO itemDto) throws NotFoundException;

    Long deleteItem(Long userId, Long itemId) throws NotFoundException;

    Collection<ItemDTO> search(String text);
}