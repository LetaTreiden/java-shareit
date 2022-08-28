package ru.practicum.item;

import ru.practicum.exceptions.NotFoundException;
import ru.practicum.user.User;

import java.util.Collection;
import java.util.Map;

public interface ItemRepository {
    Item create(Long userId, Item item, User user);

    Item findById(Long itemId);

    Collection<Item> findByUserId(Long userId);

    Map<Long, Item> findAll();

    Item update(Long itemId, Item item);

    Long delete(Long itemId);

    Collection<Item> search(String text);

    boolean checkOwner(Long userId, Long itemId);

    void checkItemId(Long itemId) throws NotFoundException;
}