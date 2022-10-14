package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

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
        return itemDto;
    }

   /* public static ItemDTOBooking toItemDtoBooking(Item item) {
        ItemDTOBooking iDtoBooking = new ItemDTOBooking();
        iDtoBooking.setId(item.getId());
        iDtoBooking.setName(item.getName());
        iDtoBooking.setDescription(item.getDescription());
        iDtoBooking.setIsAvailable(item.getIsAvailable());
        return iDtoBooking;
    }

    public static List<ItemDTOBooking> toItemBookingDtos(List<Item> items) {
        List<ItemDTOBooking> temp = new ArrayList<>();
        for (Item item : items) {
            temp.add(toItemDtoBooking(item));
        }
        return temp;
    }

     */
}