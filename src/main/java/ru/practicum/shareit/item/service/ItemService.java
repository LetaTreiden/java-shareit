package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOWithDate;

import java.util.List;

public interface ItemService {

    ItemDTO add(Long userId, ItemDTO itemDto);

    ItemDTOWithDate get(Long userId, Long itemId);

    ItemDTO update(Long userId, Long itemId, ItemDTO itemDto);

    List<ItemDTOWithDate> getAllByOwner(Long userId);

    List<ItemDTO> getAllByText(String substring);

    CommentDTO addComment(Long authorId, Long itemId, CommentDTO comment);
}
