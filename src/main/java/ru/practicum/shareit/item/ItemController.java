package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOWithDate;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDTO add(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDTO itemDto) {
        log.info("Добавления новой вещи пользователем с id {}", userId);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDTO update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                              @RequestBody ItemDTO itemDto) {
        log.info("Обновление данные о вещи");
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDTOWithDate get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получение вещи с id {}", itemId);
        return itemService.get(userId, itemId);
    }

    @GetMapping
    public List<ItemDTOWithDate> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех вещей пользователя с id {}", userId);
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDTO> getAllByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam String text) {
        log.info("Получение вещей для аренды содержащие в названии или описании текст {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.getAllByText(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDTO addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDTO itemDtoWithComment) {
        log.info("Добавление комментария для вещи с id {}", itemId);
        return itemService.addComment(userId, itemId, itemDtoWithComment);
    }
}
