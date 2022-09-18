package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService iService;

    @PostMapping
    public ItemDTO createItem(@RequestHeader(HEADER_USER_ID) Long userId, @Valid @RequestBody ItemDTO iDto)
            throws ValidationException, NotFoundException {
        return iService.createItem(userId, iDto);
    }

    @GetMapping("/{itemId}")
    public ItemDTO findItemById(@PathVariable String itemId) throws NotFoundException {
        return iService.findById(Long.valueOf(itemId));
    }

    @GetMapping
    public Collection<ItemDTO> findAll(@RequestHeader(HEADER_USER_ID) String userId)
            throws NotFoundException {
        return iService.findByUser(Long.valueOf(userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDTO updateItem(@RequestHeader(HEADER_USER_ID) String userId, @PathVariable String itemId,
                              @Valid @RequestBody ItemDTO iDto) throws NotFoundException {
        return iService.update(Long.valueOf(userId), Long.valueOf(itemId), iDto);
    }

    @DeleteMapping("/{itemId}")
    public Long deleteItem(@RequestHeader(HEADER_USER_ID) String userId, @PathVariable String itemId)
            throws NotFoundException {
        return iService.deleteItem(Long.valueOf(userId), Long.valueOf(itemId));
    }

    @GetMapping("/search")
    public List<Item> searchItemByText(@RequestParam String text) {
        return iService.search(text);
    }
}