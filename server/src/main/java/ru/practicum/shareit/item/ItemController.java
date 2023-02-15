package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDtoWithComment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto
    ) {
        log.info("Добавления новой вещи пользователем с id {}", userId);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto changeItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Обновление данные о вещи");
        return itemService.changeItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получение вещи с id {}", itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getAllOwnItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(name = "from", required = false) Integer from,
                                                   @RequestParam(name = "size", required = false) Integer size) {
        log.info("Получение всех вещей пользователя с id {}", userId);
        return itemService.getAllOwnItems(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsForRent(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam String text,
                                               @RequestParam(name = "from", required = false) Integer from,
                                               @RequestParam(name = "size", required = false) Integer size) {
        log.info("Получение вещей для аренды содержащие в названии или описании текст {}", text);
        return itemService.getItemsForRent(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ItemDtoWithComment addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                         @RequestBody ItemDtoWithComment itemDtoWithComment) {
        log.info("Добавление комментария для вещи с id {}", itemId);
        return itemService.addComment(userId, itemId, itemDtoWithComment);
    }
}
