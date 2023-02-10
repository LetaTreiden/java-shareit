package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.dto.RequestDTOWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        User user = uRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info(request.toString());
        if (request.getDescription() == null || Objects.equals(request.getDescription(), "")) {
            throw new BadRequestException("Отсутствует описание для поиска вещи");
        }
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);
        ItemRequest itemRequest = rRepository.save(RequestMapper.toItemRequest(request));
        return RequestMapper.toRequestDto(itemRequest);
    }

    @Override
    public List<RequestDTOWithItems> findAllByOwner(Long userId) {
        User user = uRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
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
        log.info("start");
        List<RequestDTOWithItems> requestForFindDtos = new ArrayList<>();
        Pageable pageable;
        Sort sortById = Sort.by(Sort.Direction.DESC, "created");
        if (page != null && size != null) {
            log.info("page and size");
            if (page < 0 || size < 0) {
                log.info("wrong from or size");
                throw new BadRequestException("From или size не могут принимать отрицательноге значение");
            }
            if (size == 0) {
                log.info("size equals null");
                throw new BadRequestException("Size не может принимать значение 0");
            }
            pageable = PageRequest.of(page / size, size, sortById);
            log.info("page");

            Page<ItemRequest> requests = rRepository.findAllBy(userId, pageable);
            log.info(requests.toString());
            for (ItemRequest request : requests) {
                log.info(request.toString());
                log.info("request");
                List<Item> items = iRepository.findByRequest(request.getId());
                log.info(String.valueOf(items.size()));
                RequestDTOWithItems request1 = RequestMapper.toRequestForFindDto(request, ItemMapper.mapToItemDto(items));
                log.info(request1.toString());
                requestForFindDtos.add(request1);
            }
            log.info("request get");
            return requestForFindDtos;
        }
        List<ItemRequest> requests = rRepository.findAll();
        for (ItemRequest request : requests) {
            log.info("request without pageable" + request.getId());
            List<Item> items = iRepository.findByRequest(request.getId());
            RequestDTOWithItems request1 = RequestMapper.toRequestForFindDto(request, ItemMapper.mapToItemDto(items));
            requestForFindDtos.add(request1);
        }
        return requestForFindDtos;
    }

    @Override
    public RequestDTOWithItems findById(Long userId, Long requestId) {
        uRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest request = rRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        List<Item> items = iRepository.findByRequest(requestId);
        return RequestMapper.toRequestForFindDto(request, ItemMapper.mapToItemDto(items));
    }
}

