package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDTOForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemDTOWithDate;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBookings;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDTO add(Long userId, ItemDTO itemDto) throws BadRequestException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = repository.save(ItemMapper.toItem(itemDto, user));
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDTO update(Long userId, Long itemId, ItemDTO itemDto) throws BadRequestException {
        Item item = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        User user = item.getOwner();
        if (Objects.equals(user.getId(), userId)) {
            if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            return ItemMapper.toItemDto(item);
        }
        throw new ForbiddenException("No rights");
    }

    @Override
    public ItemDTOWithDate get(Long userId, Long itemId) {
        Item item = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        ItemDTOWithDate itemDto = ItemMapper.mapToItemDtoWithDate(item);
        List<Comment> comments = commentRepository.findAllByItem(item);
        itemDto.setComments(CommentMapper.mapToCommentDto(comments));
        if (Objects.equals(item.getOwner().getId(), userId)) {
            List<Booking> bookings = bookingRepository.findByItemOrderByStartDesc(item);
            if (!bookings.isEmpty()) {
                Booking nextBooking = bookings.get(0);
                BookingDTOForItem bookingNext = BookingMapper.toBookingDtoForItem(nextBooking);
                itemDto.setNextBooking(bookingNext);
                if (bookings.size() >= 2) {
                    Booking lastBooking = bookings.get(1);
                    BookingDTOForItem bookingLast = BookingMapper.toBookingDtoForItem(lastBooking);
                    itemDto.setLastBooking(bookingLast);
                }
            }
        }
        return itemDto;
    }

    @Override
    public List<ItemDTOWithDate> getAllByOwner(Long userId) {
        User owner = userRepository.getReferenceById(userId);
        List<ItemWithBookings> items = repository.findAllByOwnerWithBookings(
                LocalDateTime.now(), owner.getId());
        Map<Long, List<Comment>> comments =
                commentRepository.findAllByItemIdIn(
                                items.stream()
                                        .map(ItemWithBookings::getId)
                                        .collect(Collectors.toUnmodifiableList()),
                                Sort.by(Sort.Direction.ASC, "created")).stream()
                        .collect(Collectors.groupingBy(c -> c.getItem().getId(), Collectors.toUnmodifiableList()));
        return ItemMapper.mapToItemDtoWithDate(items, comments);
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
    public CommentDTO addComment(Long authorId, Long itemId, CommentDTO itemDtoWithComment) {
        Item item = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        User author = userRepository.findById(authorId).orElseThrow(() -> new NotFoundException("User not found"));
        List<Booking> bookings = bookingRepository
                .findByItemAndBookerAndStartBeforeAndEndBefore(item, author, LocalDateTime.now(), LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadRequestException("Booking is empty");
        }
        Comment comment = CommentMapper.toComment(itemDtoWithComment, item, author);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
