package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemRequestController {

    private final ItemRequestServiceImpl requestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody ItemRequestDto request) {
        log.info("Добавление запроса на добавление вещи для пользователя {}", userId);
        return requestService.addRequest(userId, request);
    }

    @GetMapping
    public List<RequestDto> getAllOwnRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Просмотр всех запросов на добавление вещи для пользователя {}", userId);
        return requestService.findAllOwnRequest(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(name = "from", required = false) Integer from,
                                          @RequestParam(name = "size", required = false) Integer size) {
        log.info("Просмотр всех запросов на добавление вещи");
        return requestService.findAllRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("Просмотр запроса с id {}", requestId);
        return requestService.findRequest(userId, requestId);
    }
}
