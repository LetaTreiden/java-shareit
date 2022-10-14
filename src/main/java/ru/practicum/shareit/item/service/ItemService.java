package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {
        List<ItemDTO> findAllItemsByOwner(Long id);

        Collection<Item> getAllItemsByString(String someText);

        ItemDTO patchItem(ItemDTO itemDto, Long itemId, Long id);

        ItemDTO findItemById(Long userId, Long itemId);

         CommentDTO postComment(Long userId, Long itemId, CommentDTO commentDto);
    }