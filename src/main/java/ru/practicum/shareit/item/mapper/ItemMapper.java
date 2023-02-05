package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOWithDate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDTO toItemDto(Item item) {
        ItemDTO itemDto = new ItemDTO();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static ItemDTOWithDate toItemDtoWithDate(Item item) {
        ItemDTOWithDate itemDto = new ItemDTOWithDate();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;

    }

    public static Item toItem(ItemDTO itemDto, User user) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;

    }

    public static Item toItemWithDate(ItemDTOWithDate itemDto, User user) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;

    }

    public static List<ItemDTO> mapToItemDto(Iterable<Item> items) {
        List<ItemDTO> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }

    public static List<ItemDTOWithDate> mapToItemDtoWithDate(Iterable<Item> items) {
        List<ItemDTOWithDate> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDtoWithDate(item));
        }
        return dtos;
    }

    public static BookingDTOToReturn.Item toItemToBookingDTO(Item item) {
        return new BookingDTOToReturn.Item(item.getId(), item.getName());
    }
}

