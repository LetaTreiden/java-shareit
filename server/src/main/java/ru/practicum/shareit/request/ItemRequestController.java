package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.dto.RequestDTOWithItems;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestDTO addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody RequestDTO request) {
        log.info("Добавление запроса на добавление вещи для пользователя {}", userId);
        return requestService.add(userId, request);
    }

    @GetMapping
    public List<RequestDTOWithItems> getAllOwnRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Просмотр всех запросов на добавление вещи для пользователя {}", userId);
        return requestService.findAllByOwner(userId);
    }

    @GetMapping("/all")
    public List<RequestDTOWithItems> getAllRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(name = "size", defaultValue = "1") Integer size) {
        log.info("Просмотр всех запросов на добавление вещи");
        return requestService.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDTO getRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("Просмотр запроса с id {}", requestId);
        return requestService.findById(userId, requestId);
    }
}
