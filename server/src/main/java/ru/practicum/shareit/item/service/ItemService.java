package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoWithComment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDtoWithBooking getItem(Long userId, Long itemId);

    ItemDto changeItem(Long userId, Long itemId, ItemDto itemDto);

    List<ItemDtoWithBooking> getAllOwnItems(Long userId, Integer page, Integer size);

    Collection<ItemDto> getItemsForRent(String substring, Integer page, Integer size);

    ItemDtoWithComment addComment(Long authorId, Long itemId, ItemDtoWithComment comment);
}
