package ru.practicum.shareit.item.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.comment.CommentDTO;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exceptions.InvalidParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;
    Logger logger = LoggerFactory.getLogger("log");


    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserServiceImpl userService,
                           BookingRepository bookingRepository, UserRepository userRepository,
                           CommentRepository commentRepository, BookingServiceImpl bookingService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingService = bookingService;
    }

    public ItemDTO createItem(Long id, ItemDTO itemDto) {
        validateItemDto(itemDto);
        itemDto.setOwner(userService.findUserById(id));
        return ItemMapper.toIDto(itemRepository.save(ItemMapper.toItem(itemDto)));
    }

    @Override
    @Transactional
    public ItemDTO update(ItemDTO itemDTO, long id, long itemId) {
        logger.info("Процесс пошел");
        Item item = itemRepository.getReferenceById(itemId);
        logger.info("вещь есть");
        User user = userRepository.getReferenceById(id);
        logger.info("юзер есть");
        validateItem(itemId);
        logger.info("Вещь");
        validateUser(id);
        logger.info("Пользователь");
        if (item.getOwner().getId() == id) {
            item.setOwner(user);
            item.setName(itemDTO.getName() == null ? item.getName() : itemDTO.getName());
            if (itemDTO.getDescription() == null || itemDTO.getDescription().isBlank())
                item.setDescription(item.getDescription());
            else item.setDescription(itemDTO.getDescription());
            item.setIsAvailable(itemDTO.getIsAvailable() == null ? item.getIsAvailable() : itemDTO.getIsAvailable());
        } else {
            throw new NotFoundException("Изменять вещь может только её владелец");
        }
        itemSetBookingsAndComments(item);
        itemRepository.save(item);
        return ItemMapper.toIDto(item);
    }

    public ItemDTO findItemById(Long userId, Long itemId) {
        validateItem(itemId);
        Item item = itemRepository.getReferenceById(itemId);
        itemSetBookingsAndComments(item);
        if (!(item.getOwner().getId() == (userId))) {
            item.setLastBooking(null);
            item.setNextBooking(null);
        }
        logger.info("item ", item);
        return ItemMapper.toIDto(item);
    }

    @Override
    public List<ItemDTO> findAllItemsByOwner(Long id) {
        validateUser(id);
        List<Item> items = new ArrayList<>(itemRepository.findAllItemsByOwner(id));
        items.forEach(this::itemSetBookingsAndComments);
        items.stream().sorted(Comparator.comparing(Item::getId)).collect(Collectors.toList());
        return ItemMapper.toItemDtos(items);
    }

    @Override
    public List<ItemDTO> getAllItemsByString(String text) {
        List<Item> availableItems = new ArrayList<>();
        if (text.length() > 0 && !text.trim().equals("")) {
            for (Item itemFromStorage : itemRepository.findAll()) {
                if (itemFromStorage.getIsAvailable() && (itemFromStorage.getDescription().toLowerCase()
                        .contains(text.toLowerCase()) || itemFromStorage.getName().toLowerCase()
                        .contains(text.toLowerCase()))) {
                    availableItems.add(itemFromStorage);
                }
            }
        }
        return ItemMapper.toItemDtos(availableItems);
    }

    @Override
    public void validateItem(ItemDTO itemDto, Long itemId) {
        if (!(itemRepository.existsById(itemId))) {
            logger.info("товар не найден");
            throw new NotFoundException("Такого товара нет");
        }
        ItemDTO patchedItem = ItemMapper.toIDto(itemRepository.getReferenceById(itemId));
        if (itemDto.getName() != null) {
            patchedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            patchedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getIsAvailable() != null) {
            patchedItem.setIsAvailable(itemDto.getIsAvailable());
        }
    }

    public CommentDTO postComment(Long userId, Long itemId, CommentDTO commentDto) {
        validateComment(commentDto);
        validateUser(userId);
        validateItem(itemId);

        if (bookingService.checkBooking(userId, itemId, BookingStatus.APPROVED)) {
            commentRepository.save(CommentMapper.toComment(commentDto, itemRepository.getReferenceById(itemId),
                    userRepository.getReferenceById(userId)));
            logger.info("comdto " + commentDto);
            return commentDto;
        } else {
            throw new InvalidParameterException("Пользователь " + userId + " не брал вещь " + itemId + " в аренду");
        }
    }

    private void validateUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validateItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Товар не найден");
        }
    }

    private void validateItemDto(ItemDTO itemDto) {
        if (itemDto.getIsAvailable() == null) {
            throw new InvalidParameterException("Укажите доступность товара");
        } else if (itemDto.getName() == null || itemDto.getName().equals("")) {
            throw new InvalidParameterException("Название товара не может быть пустым");
        } else if (itemDto.getDescription() == null || itemDto.getDescription().equals("")) {
            throw new InvalidParameterException("Описание товара не может юыть пустым");
        }
    }

    private void validateComment(CommentDTO commentDto) {
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new InvalidParameterException("Комментарий не может юыть пусьым");
        }
    }

    private void itemSetBookingsAndComments(Item item) {
        item.setComments(new ArrayList<>(commentRepository.findAllItemComments(item.getId())));
        item.setLastBooking(bookingService.getLastBooking(item.getId()).orElse(null));
        logger.info("last " + item.getLastBooking());
        item.setNextBooking(bookingService.getNextBooking(item.getId()).orElse(null));
        logger.info("next " + item.getNextBooking());
    }
}