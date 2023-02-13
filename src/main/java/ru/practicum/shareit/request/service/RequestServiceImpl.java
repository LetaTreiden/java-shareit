package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.dto.RequestDTOWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final UserRepository uRepository;
    private final RequestRepository rRepository;
    private final ItemRepository iRepository;


    @Override
    @Transactional
    public RequestDTO add(Long userId, RequestDTO request) {
        User user = uRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        request.setCreated(LocalDateTime.now());
        request.setRequester(UserMapper.toUserToRequest(user));
        ItemRequest itemRequest = rRepository.save(RequestMapper.toItemRequest(request));
        return RequestMapper.toRequestDto(itemRequest);
    }

    @Override
    public List<RequestDTOWithItems> findAllByOwner(Long userId) {
        User user = uRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<RequestDTOWithItems> requests = new ArrayList<>();
        List<ItemRequest> itemRequests = rRepository.findByRequesterOrderByCreatedDesc(user);
        for (ItemRequest request : itemRequests) {
            List<Item> item = iRepository.findByRequest(request.getId());
            requests.add(RequestMapper.toRequestForFindDto(request, ItemMapper.mapToItemDto(item)));
        }
        return requests;
    }

    @Override
    public List<RequestDTOWithItems> findAll(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        List<RequestDTOWithItems> getAllRequests = new ArrayList<>();
        List<ItemRequest> itemRequests = rRepository.findAllByRequester_IdNot(userId, pageable);
        Map<ItemRequest, List<Item>> items = iRepository.findAllByRequestIdInAndAvailableTrue(itemRequests)
                .stream()
                .collect(groupingBy(Item::getRequestId, toList()));
        for (ItemRequest request : itemRequests) {
            RequestDTOWithItems itemRequestDtoResult;
            List<ItemDTO> itemsToReturn;
            if (!items.isEmpty()) {
                itemsToReturn = items.get(request)
                        .stream()
                        .filter(item -> item.getRequestId().getId().equals(request.getId()))
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());
                itemRequestDtoResult = RequestMapper.toRequestForFindDto(request, itemsToReturn);
                getAllRequests.add(itemRequestDtoResult);
            } else {
                itemRequestDtoResult = RequestMapper.toRequestForFindDto(request, Collections.emptyList());
                getAllRequests.add(itemRequestDtoResult);
            }
        }
        return getAllRequests;
    }

    @Override
    public RequestDTOWithItems findById(Long userId, Long requestId) {
        uRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        ItemRequest request = rRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        List<Item> items = iRepository.findByRequest(requestId);
        return RequestMapper.toRequestForFindDto(request, ItemMapper.mapToItemDto(items));
    }
}

