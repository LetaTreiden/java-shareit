package ru.practicum.item;


import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.exceptions.ValidationException;

import java.util.Collection;

public interface ItemService {
    ItemDTO createItem(Long userId, ItemDTO itemDto) throws ValidationException;

    ItemDTO findById(Long itemId) throws HttpClientErrorException.NotFound;

    Collection<ItemDTO> findByUser(Long userId) throws HttpClientErrorException.NotFound;

    ItemDTO update(Long userId, Long itemId, ItemDTO itemDto) throws HttpClientErrorException.NotFound;

    Long deleteItem(Long userId, Long itemId) throws HttpClientErrorException.NotFound;

    Collection<ItemDTO> search(String text);
}