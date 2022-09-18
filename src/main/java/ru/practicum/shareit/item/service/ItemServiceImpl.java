package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exceptions.InvalidParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService uService;
    private final UserMapper uMapper;

    private final ItemRepository iRepository;
    private final ItemMapper iMapper;

    @Override
    public ItemDTO createItem(Long uId, ItemDTO iDto) throws ValidationException, NotFoundException {
        validate(uId, iDto);
        UserDTO uDto = uService.findById(uId);
        Item item = iRepository.create(uId, iMapper.toItem(iDto), uMapper.toUser(uDto));
        log.info("Товар id {} создан", item.getId());
        return iMapper.toIDto(item);
    }

    private void validate(Long uId, ItemDTO iDto) {
        uService.checkId(uId);
        if (!StringUtils.hasText(iDto.getName())) {
            throw new InvalidParameterException("Имя не может быть пустым");
        }

        if (iDto.getDescription() == null) {
            throw new InvalidParameterException("Описание не может быть пустым");
        }

        if (iDto.getAvailable() == null) {
            throw new InvalidParameterException("Статус доступности не может быть пустым");
        }
    }

    @Override
    public ItemDTO findById(Long id) throws NotFoundException {
        iRepository.checkItemId(id);
        Item item = iRepository.findById(id);
        return iMapper.toIDto(item);
    }

    @Override
    public Collection<ItemDTO> findByUser(Long id) throws NotFoundException {
        uService.checkId(id);

        return iRepository.findByUserId(id).stream().map(iMapper::toIDto).collect(Collectors.toList());
    }

    @Override
    public ItemDTO update(Long userId, Long itemId, ItemDTO iDto) throws NotFoundException {
        uService.checkId(userId);
        iRepository.checkItemId(itemId);

        if (iRepository.checkOwner(userId, itemId)) {
            throw new NotFoundException("Неправльный владелец");
        }

        Item item = iRepository.update(itemId, iMapper.toItem(iDto));
        log.info("Обновлено id {}", item.getId());
        return iMapper.toIDto(item);
    }

    @Override
    public Long deleteItem(Long userId, Long itemId) throws NotFoundException {
        uService.checkId(userId);
        iRepository.checkItemId(itemId);

        Long itemDeletedId = iRepository.delete(itemId);
        log.info("Удалено id {}", itemDeletedId);
        return itemDeletedId;
    }

    @Override
    public List<Item> search(String text) {
        List<Item> availableItems = new ArrayList<>();
        if (text.length() > 0 && !text.trim().equals("")) {
            for (Item itemFromStorage : iRepository.findAll().values()) {
                if (itemFromStorage.getAvailable() && (itemFromStorage.getDescription().toLowerCase().contains(text.toLowerCase()) || itemFromStorage.getName().toLowerCase().contains(text.toLowerCase()))) {
                    availableItems.add(itemFromStorage);
                }
            }
        }
        return availableItems;
    }
}
