package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository repository;
    private final ItemRepository itemRepository;


    @Override
    public ItemRequestDto addRequest(Long userId, ItemRequestDto request) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (request.getDescription() == null || Objects.equals(request.getDescription(), "")) {
            throw new BadRequestException("Отсутствует описание для поиска вещи");
        }
        request.setCreated(LocalDateTime.now());
        request.setRequestor(user.get());
        ItemRequest itemRequest = repository.save(ItemRequestMapper.toItemRequest(request));
        return ItemRequestMapper.toRequestDto(itemRequest);
    }

    @Override
    public List<RequestDto> findAllOwnRequest(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        List<RequestDto> requests = new ArrayList<>();
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<ItemRequest> itemRequests = repository.findByRequestorOrderByCreatedDesc(user.get());
        for (ItemRequest request : itemRequests) {
            List<Item> item = itemRepository.findItemByRequest(request.getId());
            requests.add(ItemRequestMapper.toRequestForFindDto(request, ItemMapper.mapToItemDto(item)));
        }

        return requests;
    }

    @Override
    public List<RequestDto> findAllRequest(Long userId, Integer page, Integer size) {
        List<RequestDto> requestForFindDtos = new ArrayList<>();
        Pageable pageable;
        Sort sortById = Sort.by(Sort.Direction.DESC, "created");
        if (page != null && size != null) {
            pageable = PageRequest.of(page / size, size, sortById);

            Page<ItemRequest> requests = repository.findAllBy(userId, pageable);
            for (ItemRequest request : requests) {
                List<Item> items = itemRepository.findByRequestId(request.getId());
                RequestDto request1 = ItemRequestMapper.toRequestForFindDto(request, ItemMapper.mapToItemDto(items));
                requestForFindDtos.add(request1);
            }

            return requestForFindDtos;
        }
        List<ItemRequest> requests = repository.findAll();
        for (ItemRequest request : requests) {
            List<Item> items = itemRepository.findByRequestId(request.getId());
            RequestDto request1 = ItemRequestMapper.toRequestForFindDto(request, ItemMapper.mapToItemDto(items));
            requestForFindDtos.add(request1);
        }
        return requestForFindDtos;
    }

    @Override
    public RequestDto findRequest(Long userId, Long requestId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Optional<ItemRequest> request = repository.findById(requestId);
        if (request.isEmpty()) {
            throw new NotFoundException("Запрос не найден");
        }
        List<Item> items = itemRepository.findByRequestId(requestId);
        return ItemRequestMapper.toRequestForFindDto(request.get(),
                ItemMapper.mapToItemDto(items));
    }
}
