package ru.practicum.item;


import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.exceptions.ValidationException;

import java.util.Collection;

@Repository
public interface ItemService {
    ItemDTO createItem(Long userId, ItemDTO itemDto) throws ValidationException, ClassNotFoundException;

    ItemDTO findById(Long itemId) throws HttpClientErrorException.NotFound;

    Collection<ItemDTO> findByUser(Long userId) throws HttpClientErrorException.NotFound, ClassNotFoundException;

    ItemDTO update(Long userId, Long itemId, ItemDTO itemDto) throws HttpClientErrorException.NotFound, ClassNotFoundException;

    Long deleteItem(Long userId, Long itemId) throws HttpClientErrorException.NotFound, ClassNotFoundException;

    Collection<ItemDTO> search(String text);
}