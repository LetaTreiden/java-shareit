package ru.practicum.shareit.item.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDTO;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exceptions.InvalidParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    Logger logger = LoggerFactory.getLogger("log");


    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserServiceImpl userService,
                           BookingRepository bookingRepository, UserRepository userRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public ItemDTO createItem(Long id, ItemDTO itemDto) {
        validateItemDto(itemDto);
        itemDto.setOwner(userService.findUserById(id));
        return ItemMapper.toIDto(itemRepository.save(ItemMapper.toItem(itemDto)));
    }

    public ItemDTO updateItem(ItemDTO itemDto, Long uId, Long iId) {
        validateItem(itemDto, iId);
        Item temp = itemRepository.getReferenceById(iId);
        if (!temp.getOwner().getId().equals(uId)) {
            logger.info("owner id is " + uId);
            throw new ValidationException("Данный пользователь не может изменить товар");
        }
        if (!Objects.equals(temp.getName(), itemDto.getName())) {
            temp.setName(itemDto.getName());
            logger.info("Имя обноалено");
        }
        if (!Objects.equals(itemDto.getIsAvailable(), temp.getIsAvailable())) {
            temp.setIsAvailable(itemDto.getIsAvailable());
            logger.info("Статус обноален");
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().equals("")) {
            temp.setDescription(itemDto.getDescription());
            logger.info("Описание обновлено");
        }
        return ItemMapper.toIDto(itemRepository.save(temp));
    }

    public ItemDTO findItemById(Long userId, Long itemId) {
        validateItem(itemId);
        ItemDTO itemDto = ItemMapper.toIDto(itemRepository.getReferenceById(itemId));
        Set<CommentDTO> comments = CommentMapper.toCommentDtos(commentRepository.findAllItemComments(itemId));
        for (CommentDTO commentDto : comments) {
            itemDto.getComments().add(commentDto);
            for (CommentDTO commentDtoName : itemDto.getComments()) {
                commentDtoName.setAuthorName(commentDtoName.getAuthor().getName());
            }
        }

        if (Objects.equals(itemRepository.getReferenceById(itemId).getOwner().getId(), userId)) {
            List<Booking> bookingPast = bookingRepository.findAllItemBookingsPast(itemId);
            if (bookingPast.size() != 0) {
                bookingPast.sort(Comparator.comparing(Booking::getStart).reversed());
                BookingDTO bookingDtoPast = BookingMapper.toBookingDto(bookingPast.get(0));
                bookingDtoPast.setBooker(bookingDtoPast.getBooker());
                bookingDtoPast.setBooker(null);
                itemDto.setLastBooking(bookingDtoPast);
            }
            List<Booking> bookingFuture = bookingRepository.findAllItemBookingsFuture(itemId);
            if (bookingFuture.size() != 0) {
                bookingFuture.sort(Comparator.comparing(Booking::getStart));
                BookingDTO bookingDtoFuture = BookingMapper.toBookingDto(bookingFuture.get(0));
                bookingDtoFuture.setBooker(bookingDtoFuture.getBooker());
                bookingDtoFuture.setBooker(null);
                itemDto.setNextBooking(bookingDtoFuture);
            }
        }
        return itemDto;
    }

    @Override
    public List<ItemDTO> findAllItemsByOwner(Long id) {
        validateUser(id);

        List<ItemDTO> itemDtoList = ItemMapper.toItemDtos(itemRepository.findAllItemsByOwner(id));

        for (ItemDTO itemDto : itemDtoList) {
            List<Booking> bookingPast = bookingRepository.findAllItemBookingsPast(itemDto.getId());
            if (bookingPast.size() != 0) {
                bookingPast.sort(Comparator.comparing(Booking::getStart).reversed());
                BookingDTO bookingDtoPast = BookingMapper.toBookingDto(bookingPast.get(0));
                bookingDtoPast.setBooker(bookingDtoPast.getBooker());
                itemDto.setLastBooking(bookingDtoPast);
            }
            List<Booking> bookingFuture = bookingRepository.findAllItemBookingsFuture(itemDto.getId());
            if (bookingFuture.size() != 0) {
                bookingFuture.sort(Comparator.comparing(Booking::getStart));
                BookingDTO bookingDtoFuture = BookingMapper.toBookingDto(bookingFuture.get(0));
                bookingDtoFuture.setBooker(bookingDtoFuture.getBooker());
                itemDto.setNextBooking(bookingDtoFuture);
            }
        }
        itemDtoList.sort(Comparator.comparing(ItemDTO::getId));
        return itemDtoList;
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
        ItemDTO patchedItem = findItemById(itemId, itemDto.getId());
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

        List<Booking> bookings = bookingRepository.findAllItemBookings(itemId);
        for (Booking booking : bookings) {
            if (!Objects.equals(booking.getBooker().getId(), userId)) {
                throw new InvalidParameterException("Проверьте заданные параметры");
            } else {
                if (!booking.getStart().isBefore(LocalDateTime.now()) &&
                        !booking.getEnd().isBefore(LocalDateTime.now()) &&
                        booking.getStatus().equals(BookingStatus.PAST)) {
                    commentDto.setItem(ItemMapper.toIDto(itemRepository.getReferenceById(itemId)));
                    commentDto.setAuthor(UserMapper.toUserDto(userRepository.getReferenceById(userId)));
                    commentDto.setCreated(LocalDateTime.now());
                    Comment commentTemp = commentRepository.save(CommentMapper.toComment(commentDto));
                    CommentDTO commentTempDto = CommentMapper.toCommentDto(commentTemp);
                    User user = userRepository.getReferenceById(userId);
                    commentTempDto.setAuthorName(user.getName());
                    commentTempDto.setAuthor(null);
                    commentTempDto.setItem(null);
                    return commentTempDto;
                } else {
                    throw new InvalidParameterException("Проверьте заданные параметры");
                }
            }
        }
        return null;
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

}