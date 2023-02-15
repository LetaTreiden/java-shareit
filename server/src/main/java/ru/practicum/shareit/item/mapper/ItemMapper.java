package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner());
        itemDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return itemDto;
    }

    public static ItemDtoWithBooking toItemDtoWithDate(Item item) {
        ItemDtoWithBooking itemDto = new ItemDtoWithBooking();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner());
        return itemDto;

    }

    public static Item toItem(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;

    }

    public static Item toItem(ItemDtoWithBooking itemDtoWithBooking) {
        Item item = new Item();
        item.setId(itemDtoWithBooking.getId());
        item.setName(itemDtoWithBooking.getName());
        item.setDescription(itemDtoWithBooking.getDescription());
        item.setAvailable(itemDtoWithBooking.getAvailable());
        item.setOwner(itemDtoWithBooking.getOwner());
        return item;

    }

    public static Item toItemWithDate(ItemDtoWithBooking itemDto, User user) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;

    }

    public static List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }

    public static List<Item> mapToItem(Iterable<ItemDtoWithBooking> items) {
        List<Item> dtos = new ArrayList<>();
        for (ItemDtoWithBooking item : items) {
            dtos.add(toItem(item));
        }
        return dtos;
    }

    public static List<ItemDtoWithBooking> mapToItemDtoWithDate(Iterable<Item> items) {
        List<ItemDtoWithBooking> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDtoWithDate(item));
        }
        return dtos;
    }
}

