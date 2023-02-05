package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDTOForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDTOWithComment;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOWithDate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDTO add(Long userId, ItemDTO itemDto) throws BadRequestException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        Item item = repository.save(ItemMapper.toItem(itemDto, user.get()));
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDTO update(Long userId, Long itemId, ItemDTO itemDto) throws BadRequestException {
        Optional<Item> itemOpt = repository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
        Item item = itemOpt.get();
        User user = item.getOwner();
        if (Objects.equals(user.getId(), userId)) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            repository.save(item);
            return ItemMapper.toItemDto(item);
        }
        throw new ForbiddenException("No rights");
    }

    @Override
    public ItemDTOWithDate get(Long userId, Long itemId) {
        Optional<Item> itemOpt = repository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
        Item item = itemOpt.get();
        ItemDTOWithDate itemDto = ItemMapper.toItemDtoWithDate(item);
        List<Comment> comments = commentRepository.findByItem(item);
        itemDto.setComments(CommentMapper.mapToCommentDto(comments));
        if (Objects.equals(item.getOwner().getId(), userId)) {
            List<Booking> bookings = bookingRepository.findByItemOrderByStartDesc(item);
            if (!bookings.isEmpty()) {

                LocalDateTime next = bookings.get(0).getStart();
                Booking nextBooking = bookings.get(0);
                BookingDTOForItem bookingNext = BookingMapper.toBookingDtoForItem(nextBooking, next);
                itemDto.setNextBooking(bookingNext);
                if (bookings.size() >= 2) {
                    LocalDateTime last = bookings.get(1).getStart();
                    Booking lastBooking = bookings.get(1);
                    BookingDTOForItem bookingLast = BookingMapper.toBookingDtoForItem(lastBooking, last);
                    itemDto.setLastBooking(bookingLast);
                }
            }
        }
        return itemDto;
    }

    @Override
    public List<ItemDTOWithDate> getAllByOwner(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = userOpt.get();
        List<Item> items = repository.findByOwner(user);
        List<ItemDTOWithDate> itemDtos = ItemMapper.mapToItemDtoWithDate(items);
        for (ItemDTOWithDate itemDto : itemDtos) {
            List<Comment> comments = commentRepository.findByItem(ItemMapper.toItemWithDate(itemDto, user));
            itemDto.setComments(CommentMapper.mapToCommentDto(comments));
            List<Booking> bookings = bookingRepository.findByItemOrderByStartDesc(ItemMapper.toItemWithDate(itemDto, user));
            if (!bookings.isEmpty()) {
                LocalDateTime next = bookings.get(0).getStart();
                LocalDateTime last = bookings.get(1).getStart();
                Booking nextBooking = bookings.get(0);
                Booking lastBooking = bookings.get(1);
                BookingDTOForItem bookingNext = BookingMapper.toBookingDtoForItem(nextBooking, next);
                BookingDTOForItem bookingLast = BookingMapper.toBookingDtoForItem(lastBooking, last);
                itemDto.setNextBooking(bookingNext);
                itemDto.setLastBooking(bookingLast);
            }
        }
        return itemDtos;
    }

    @Override
    public List<ItemDTO> getAllByText(String substring) {
        if (!Objects.equals(substring, "")) {
            return ItemMapper.mapToItemDto(repository.findItemsByNameOrDescription(substring));
        }
        return new ArrayList<>();
    }

    @Transactional
    @Override
    public ItemDTOWithComment addComment(Long authorId, Long itemId, ItemDTOWithComment itemDtoWithComment) {
        Optional<Item> itemOpt = repository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
        Item item = itemOpt.get();
        Optional<User> author = userRepository.findById(authorId);
        if (author.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        List<Booking> bookings = bookingRepository.findByItemAndBookerAndStartBeforeAndEndBefore(item, author.get(),
                LocalDateTime.now(), LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadRequestException("Booking is empty");
        }
        Comment comment = CommentMapper.toComment(itemDtoWithComment, item, author.get());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
