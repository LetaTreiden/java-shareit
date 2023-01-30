package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDTO;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Item toItem(ItemDTO itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setIsAvailable(itemDto.getIsAvailable());
        item.setOwner(UserMapper.toUser(itemDto.getOwner()));
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static ItemDTO toIDto(Item item) {
        ItemDTO itemDto = new ItemDTO();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setIsAvailable(item.getIsAvailable());
        itemDto.setOwner(UserMapper.toUserDto(item.getOwner()));
        itemDto.setRequestId(item.getRequestId());
        if (item.getLastBooking() != null) {
            itemDto.setLastBooking(BookingMapper.toBookingToItem(item.getLastBooking()));
        }
        if (item.getNextBooking() != null) {
            itemDto.setNextBooking(BookingMapper.toBookingToItem(item.getNextBooking()));
        }
        if (item.getComments() != null) {
            Set<CommentDTO> commentDTOS = new HashSet<>();
            for (Comment comment : item.getComments()) {
                commentDTOS.add(CommentMapper.toCommentDto(comment));
            }
            itemDto.setComments(commentDTOS);
        }
        return itemDto;
    }

    public static List<ItemDTO> toItemDtos(List<Item> items) {
        List<ItemDTO> tempList = new ArrayList<>();
        for (Item item : items) {
            tempList.add(toIDto(item));
        }
        return tempList;
    }


    public static List<ItemDTOBooking> toItemBookingDtos(List<Item> items) {
        List<ItemDTOBooking> temp = new ArrayList<>();
        for (Item item : items) {
            temp.add(toItemDtoBooking(item));
        }
        return temp;
    }

    public static ItemDTOBooking toItemDtoBooking(Item item) {
        ItemDTOBooking iDtoBooking = new ItemDTOBooking();
        iDtoBooking.setId(item.getId());
        iDtoBooking.setName(item.getName());
        return iDtoBooking;
    }

}