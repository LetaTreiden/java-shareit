package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;

import java.util.List;

public interface ItemService {
    List<ItemDTO> findAllItemsByOwner(Long id);

    List<ItemDTO> getAllItemsByString(String someText);

    void validateItem(ItemDTO itemDto, Long itemId);

    ItemDTO findItemById(Long userId, Long itemId);

    CommentDTO postComment(Long userId, Long itemId, CommentDTO commentDto);
}