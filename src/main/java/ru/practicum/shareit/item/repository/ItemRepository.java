package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;

public interface ItemRepository {
    Item create(Long uId, Item item, User user);

    Item findById(Long itemId);

    Collection<Item> findByUserId(Long userId);

    Map<Long, Item> findAll();

    Item update(Long itemId, Item item);

    Long delete(Long itemId);

    Collection<Item> search(String text);

    boolean checkOwner(Long userId, Long itemId);

    void checkItemId(Long itemId) throws NotFoundException;
}