package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemInMemoryStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    @Override
    public ItemDTO addItem(User user, ItemDTO itemDto) {
        id = id + 1;
        itemDto.setId(id);
        items.put(id, ItemMapper.toItem(itemDto, user));
        log.info("Добавлена новая вещь: {},", itemDto.getName());
        return itemDto;
    }

    @Override
    public ItemDTO changeItem(Long userId, Long itemId, ItemDTO itemDto) {
        Item item = items.get(itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        items.put(itemId, item);
        log.info("Обновлены данные для вещи: {}", itemDto.getName());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Optional<ItemDTO> getItem(Long userId, Long itemId) {
        return Optional.of(ItemMapper.toItemDto(items.get(itemId)));
    }

    @Override
    public Collection<ItemDTO> getAllOwnItems(Long userId) {
        List<ItemDTO> itemsDto = new ArrayList<>();
        for (Item item : items.values()) {
            if (Objects.equals(item.getOwner().getId(), userId)) {
                itemsDto.add(ItemMapper.toItemDto(item));
            }
        }
        log.info("Колличество найденных вещей: {}", itemsDto.size());
        return itemsDto;
    }

    @Override
    public Collection<ItemDTO> getItemsForRent(String substring) {
        List<ItemDTO> itemsDto = new ArrayList<>();
        if (!substring.equals("")) {
            for (Item item : items.values()) {
                if ((item.getName().toLowerCase().contains(substring.toLowerCase())
                        || item.getDescription().toLowerCase().contains(substring.toLowerCase()))
                        && item.getAvailable()) {
                    itemsDto.add(ItemMapper.toItemDto(item));
                }
            }
        }
        log.info("Колличество найденных вещей: {}", itemsDto.size());
        return itemsDto;
    }

    @Override
    public Optional<Item> getItemFromMap(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }
}
