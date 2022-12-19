package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    @Autowired
    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDTO createItem(@RequestHeader("X-Sharer-User-Id") Long id,
                              @RequestBody ItemDTO itemDto) {
        return itemService.createItem(id, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDTO updateItem(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable Long itemId,
                              @RequestBody ItemDTO itemDto) {
        return itemService.updateItem(itemDto, id);
    }

    @GetMapping("/{itemId}")
    public ItemDTO findItemById(@RequestHeader("X-Sharer-User-Id") Long id,
                                @PathVariable Long itemId) {
        return itemService.findItemById(id, itemId);
    }

    @GetMapping
    public List<ItemDTO> findAllItems(@RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.findAllItemsByOwner(id);
    }

    @GetMapping("/search")
    public List<ItemDTO> findItemByString(@RequestParam String text) {
        return itemService.getAllItemsByString(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO postComment(@RequestHeader("X-Sharer-User-Id") Long id,
                                  @PathVariable Long itemId,
                                  @RequestBody CommentDTO commentDto) {
        return itemService.postComment(id, itemId, commentDto);
    }
}