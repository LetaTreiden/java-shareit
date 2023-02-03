package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDTOWithComment;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOWithDate;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDTO addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDTO itemDto) {
        log.info("Добавления новой вещи пользователем с id {}", userId);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDTO changeItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                              @RequestBody ItemDTO itemDto) {
        log.info("Обновление данные о вещи");
        return itemService.changeItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDTOWithDate getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получение вещи с id {}", itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDTOWithDate> getAllOwnItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех вещей пользователя с id {}", userId);
        return itemService.getAllOwnItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDTO> getItemsForRent(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam String text) {
        log.info("Получение вещей для аренды содержащие в названии или описании текст {}", text);
        return itemService.getItemsForRent(text);
    }

    @PostMapping("{itemId}/comment")
    public ItemDTOWithComment addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                         @Valid @RequestBody ItemDTOWithComment itemDtoWithComment) {
        log.info("Добавление комментария для вещи с id {}", itemId);
        return itemService.addComment(userId, itemId, itemDtoWithComment);
    }
}
