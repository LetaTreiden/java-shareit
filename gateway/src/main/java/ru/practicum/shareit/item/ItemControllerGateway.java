package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoGateway;
import ru.practicum.shareit.item.dto.ItemDtoGateway;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemControllerGateway {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemDtoGateway itemDto) {
        log.info("Creating item {},userId={}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> changeItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                             @RequestBody ItemDtoGateway itemDto) {
        log.info("Updating item {}, userId={}", itemDto, userId);
        return itemClient.changeItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", required = false,
                                                         defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", required = false,
                                                         defaultValue = "10") Integer size) {
        log.info("Get items userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllOwnItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsForRent(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam String text,
                                                  @PositiveOrZero @RequestParam(name = "from", required = false,
                                                          defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", required = false,
                                                          defaultValue = "10") Integer size) {
        log.info("Get items with text={}, userId={}, from={}, size={}", text, userId, from, size);
        if (text.isBlank()) {
            return (ResponseEntity<Object>) List.of(null);
        }
        return itemClient.getItemsForRent(userId, text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                             @Valid @RequestBody CommentDtoGateway itemDtoWithComment) {
        log.info("Creating comment {} for itemId={}, userId={}", itemDtoWithComment.getText(), itemId, userId);
        return itemClient.addComment(userId, itemId, itemDtoWithComment);
    }

}
