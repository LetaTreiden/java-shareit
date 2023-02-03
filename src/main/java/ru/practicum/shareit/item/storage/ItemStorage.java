package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;

public interface ItemStorage {

    ItemDTO addItem(User user, ItemDTO itemDto);

    ItemDTO changeItem(Long userId, Long itemId, ItemDTO itemDto);

    Optional<ItemDTO> getItem(Long userId, Long itemId);

    Collection<ItemDTO> getAllOwnItems(Long userId);

    Collection<ItemDTO> getItemsForRent(String substring);

    Optional<Item> getItemFromMap(Long itemId);
}
