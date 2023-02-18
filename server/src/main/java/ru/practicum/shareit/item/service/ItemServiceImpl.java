package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.item.dto.ItemDTOWithBookings;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public ItemDTO add(Long userId, ItemDTO itemDto) throws BadRequestException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user, requestRepository));
        log.info(item.toString());
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
    public ItemDTOWithBookings get(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        ItemDTOWithBookings itemDto = ItemMapper.toItemDtoWithBookings(item);
        List<Comment> comments = commentRepository.findAllByItem(item);
        itemDto.setComments(CommentMapper.mapToCommentDto(comments));
        if (Objects.equals(item.getOwner().getId(), userId)) {
            Booking nextBooking = bookingRepository.findFirstByStatusAndItemAndStartIsAfter(Status.APPROVED, item,
                    LocalDateTime.now(), Sort.by(ASC, "start"));
            Booking lastBooking = bookingRepository.findFirstByStatusAndItemAndStartLessThanEqual(Status.APPROVED, item,
                    LocalDateTime.now(), Sort.by(ASC, "end"));
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toBookingDtoForItem(nextBooking.getId(),
                        nextBooking.getBooker().getId()));
            }
            if (lastBooking != null) itemDto.setLastBooking(BookingMapper.toBookingDtoForItem(lastBooking.getId(),
                    lastBooking.getBooker().getId()));
        }
        return itemDto;
    }

    @Override
    public List<ItemDTOWithBookings> getAllByOwner(Long userId, Integer page, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<Item> items = new ArrayList<>();
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page / size, size);
            Page<Item> itemsPage = itemRepository.findByOwner(user, pageable);
            for (Item item : itemsPage) {
                items.add(item);
            }
        } else {
            items = itemRepository.findByOwner(user);
        }

        log.info(userId.toString());

        log.info(String.valueOf(items.size()));
        Map<Item, List<Booking>> approvedBookings =
                bookingRepository.findApprovedForItems(items, Sort.by(DESC, "start"))
                        .stream()
                        .collect(groupingBy(Booking::getItem, toList()));
        log.info(approvedBookings.toString());
        List<ItemDTOWithBookings> results = new ArrayList<>();
        Map<Long, List<Comment>> comments =
                commentRepository.findAllByItemIdIn(
                                items.stream()
                                        .map(Item::getId)
                                        .collect(Collectors.toUnmodifiableList()),
                                Sort.by(DESC, "created")).stream()
                        .collect(groupingBy(c -> c.getItem().getId(), Collectors.toUnmodifiableList()));
        log.info(comments.toString());
        for (Item item : items) {
            ItemDTOWithBookings itemInfo = ItemMapper.toDtoWithBookings(
                    item,
                    approvedBookings.getOrDefault(item, Collections.emptyList()),
                    comments.getOrDefault(item, Collections.emptyList())
            );
            log.info(itemInfo.toString());
            results.add(itemInfo);
        }
        return results;
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
    public CommentDTO addComment(Long authorId, Long itemId, CommentDTO comment) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        User author = userRepository.findById(authorId).orElseThrow(() -> new NotFoundException("User not found"));
        List<Booking> bookings = bookingRepository
                .findByItemAndBookerAndStartBeforeAndEndBefore(item, author, LocalDateTime.now(), LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadRequestException("Booking is empty");
        }
        if (comment.getText().isEmpty()) {
            throw new BadRequestException("Comment is empty");
        }
        Comment comment1 = CommentMapper.toComment(comment, item, author);
        return CommentMapper.toCommentDto(commentRepository.save(comment1));
    }

    @Override
    public List<ItemDTO> getForRent(String substring, Integer page, Integer size) {
        if (!Objects.equals(substring, "")) {
            if (page != null && size != null) {
                Pageable pageable = PageRequest.of(page / size, size);
                return ItemMapper.mapToItemDto(itemRepository.findItemsByNameOrDescription(substring, pageable));
            }
            return ItemMapper.mapToItemDto(itemRepository.findItemsByNameOrDescription(substring));
        }
        return new ArrayList<>();
    }
}
