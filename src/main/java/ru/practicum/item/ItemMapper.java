package ru.practicum.item;

import org.springframework.stereotype.Service;

@Service
public class ItemMapper {
    public ItemDTO toIDto(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();
    }

    public Item toItem(ItemDTO itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequest() != null ? itemDto.getRequest() : null)
                .build();
    }
}