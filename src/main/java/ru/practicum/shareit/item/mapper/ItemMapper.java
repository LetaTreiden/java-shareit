package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
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

    public static ItemDTOWithBookings toItemDtoWithBookings(Item item) {
        ItemDTOWithBookings itemDto = new ItemDTOWithBookings();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(UserMapper.toUserToItemWithBookingsDto(item.getOwner()));
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

    public static ItemDTOWithBookings toDtoWithBookings(Item item, List<Booking> bookings,
                                                        List<Comment> comments) {
        ItemDTOWithBookings iDTO = new ItemDTOWithBookings();
        iDTO.setId(item.getId());
        iDTO.setName(item.getName());
        iDTO.setDescription(item.getDescription());
        iDTO.setAvailable(item.getAvailable());
        bookings.stream()
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .findFirst()
                .ifPresent(lastBooking -> iDTO.setLastBooking(BookingMapper
                        .toBookingDtoForItem(lastBooking.getId(), lastBooking.getBooker().getId())));

        bookings.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .reduce((first, second) -> second)
                .ifPresent(nextBooking -> iDTO.setNextBooking(BookingMapper
                        .toBookingDtoForItem(nextBooking.getId(), nextBooking.getBooker().getId())));

        iDTO.setComments(CommentMapper.mapToCommentDto(comments));
        return iDTO;
    }
}


