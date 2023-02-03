package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDTOWithComment;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOWithDate;

import java.util.Collection;

public interface ItemService {

    ItemDTO addItem(Long userId, ItemDTO itemDto);

    ItemDTOWithDate getItem(Long userId, Long itemId);

    ItemDTO changeItem(Long userId, Long itemId, ItemDTO itemDto);

    Collection<ItemDTOWithDate> getAllOwnItems(Long userId);

    Collection<ItemDTO> getItemsForRent(String substring);

    ItemDTOWithComment addComment(Long authorId, Long itemId, ItemDTOWithComment comment);
}
