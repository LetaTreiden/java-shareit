package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOWithBookings;
import ru.practicum.shareit.item.dto.CommentDTO;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    ItemDTO add(Long userId, ItemDTO itemDto);

    ItemDTOWithBookings get(Long userId, Long itemId);

    ItemDTO update(Long userId, Long itemId, ItemDTO itemDto);

    List<ItemDTOWithBookings> getAllByOwner(Long userId, Integer page, Integer size);

    List<ItemDTO> getAllByText(String substring);

    CommentDTO addComment(Long authorId, Long itemId, CommentDTO comment);

    Collection<ItemDTO> getForRent(String substring, Integer page, Integer size);
}
