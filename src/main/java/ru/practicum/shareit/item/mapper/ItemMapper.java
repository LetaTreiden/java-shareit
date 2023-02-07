package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDTOForItem;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOWithDate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBookings;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static ItemDTOWithDate mapToItemDtoWithDate(Item item) {
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

    public static Item toItem(ItemDTOWithDate itemDto, UserRepository uRepo) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(uRepo.getReferenceById(itemDto.getOwner().getId()));
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

    public static BookingDTOToReturn.Item toItemToBookingDTO(Item item) {
        return new BookingDTOToReturn.Item(item.getId(), item.getName());
    }

    public static List<ItemDTOWithDate> mapToItemDtoWithDate(List<ItemWithBookings> items,
                                                             Map<Long, List <Comment>> comments){
        return items.stream()
                .map(i -> toItemDtoWithDate2(i, comments.get(i.getId())))
                .collect(Collectors.toUnmodifiableList());
    }

    public static ItemDTOWithDate toItemDtoWithDate2(ItemWithBookings item, List<Comment> comments) {
        ItemDTOWithDate dto = toDtoWithDateFromWithBookings(item);
        List<Comment> commentsList = new ArrayList<>();

        if (comments != null) {
            commentsList.addAll(comments);
        }
        dto.setComments(CommentMapper.mapToCommentDto(commentsList));
        return dto;
    }

    public static ItemDTOWithDate toDtoWithDateFromWithBookings(ItemWithBookings item) {
        ItemDTOWithDate iDTO = new ItemDTOWithDate();
        iDTO.setId(item.getId());
        iDTO.setName(item.getName());
        iDTO.setDescription(item.getDescription());
        iDTO.setAvailable(item.getAvailable());
        if (item.getLastBookingId() != null) {
            iDTO.setLastBooking(new BookingDTOForItem(item.getLastBookingId(), item.getLastBookerId()));
        }
        if (item.getNextBookingId() != null) {
            iDTO.setNextBooking(new BookingDTOForItem(item.getNextBookingId(), item.getNextBookerId()));
        }
        iDTO.setComments(Collections.emptyList());

        return iDTO;
    }

}


