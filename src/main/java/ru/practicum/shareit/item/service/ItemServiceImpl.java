package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDTO add(Long userId, ItemDTO itemDto) throws BadRequestException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user));
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDTO update(Long userId, Long itemId, ItemDTO itemDto) throws BadRequestException {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
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
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        ItemDTOWithDate itemDto = ItemMapper.mapToItemDtoWithDate(item);
        List<Comment> comments = commentRepository.findAllByItem(item);
        itemDto.setComments(CommentMapper.mapToCommentDto(comments));
        if (Objects.equals(item.getOwner().getId(), userId)) {
            Booking nextBooking = bookingRepository.findFirstByStatusAndItemAndStartIsAfter(Status.APPROVED, item,
                    LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
            Booking lastBooking = bookingRepository.findLastByStatusAndItemAndEndIsBefore(Status.APPROVED, item,
                    LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "end"));
            if (nextBooking != null){
                itemDto.setNextBooking(BookingMapper.toBookingDtoForItem(nextBooking));
            }
            if (lastBooking != null) itemDto.setLastBooking(BookingMapper.toBookingDtoForItem(lastBooking));
        }
        return itemDto;
    }

    @Override
    public List<ItemDTOWithDate> getAllByOwner(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        log.info(userId.toString());
        List<ItemWithBookings> items = itemRepository.findAllByOwner(
                LocalDateTime.now(), userId, String.valueOf(Status.APPROVED));
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
        if (substring.isBlank()) {
            return Collections.emptyList();
        }
            return ItemMapper.mapToItemDto(itemRepository.findItemsByNameOrDescription(substring));
    }

    @Transactional
    @Override
    public CommentDTO addComment(Long authorId, Long itemId, CommentDTO itemDtoWithComment) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
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
