package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.annotation.DirtiesContext;
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
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
@Slf4j
class ItemServiceTest {

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    RequestRepository requestRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingRepository bookingRepository;

    Item item = new Item();
    User userOwner = new User();
    User requester = new User();
    ItemRequest request = new ItemRequest();
    Booking booking = new Booking();
    Comment comment = new Comment();


    @Test
    void addItemTest() {
        addItem();
        addRequest();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userOwner));

        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(item);

        Optional<ItemDTO> itemDto = Optional.ofNullable(itemService.add(userOwner.getId(),
                ItemMapper.toItemDto(item)));

        assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(addItemTest -> {
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("id", item.getId());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("name", item.getName());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("available", item.getAvailable());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("description",
                                    item.getDescription());
                        }
                );
    }

    @Test
    void addItemWithRequestTest() {
        addItem();
        addRequest();
        item.setRequestId(request);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userOwner));

        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(item);

        Optional<ItemDTO> itemDto = Optional.ofNullable(itemService.add(userOwner.getId(),
                ItemMapper.toItemDto(item)));

        assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(addItemTest -> {
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("id", item.getId());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("name", item.getName());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("available", item.getAvailable());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("requestId",
                                    item.getRequestId().getId());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("description",
                                    item.getDescription());
                        }
                );
    }

    @Test
    void addItemUserNotFoundTest() {
        addItem();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.add(3L, ItemMapper.toItemDto(item)));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void changeItemTest() {
        addItem();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Optional<ItemDTO> itemDto = Optional.ofNullable(itemService.update(userOwner.getId(), item.getId(),
                ItemMapper.toItemDto(item)));

        assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(addItemTest -> {
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("id", item.getId());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("name", item.getName());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("available", item.getAvailable());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("description",
                                    item.getDescription());
                        }
                );
    }

    @Test
    void changeItemNotFoundTest() {
        addItem();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.update(1L, 3L, ItemMapper.toItemDto(item)));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void changeItemForbiddenExceptionTest() {
        addItem();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        final ForbiddenException exception = Assertions.assertThrows(
                ForbiddenException.class,
                () -> itemService.update(5L, 1L, ItemMapper.toItemDto(item)));

        Assertions.assertEquals("No rights", exception.getMessage());
    }

    @Test
    void getItemTest() {
        addItem();
        List<Comment> comments = new ArrayList<>();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(commentRepository.findAllByItem(any()))
                .thenReturn(comments);

        Optional<ItemDTOWithBookings> itemDto = Optional.ofNullable(itemService.get(userOwner.getId(), item.getId()));

        assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(addItemTest -> {
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("id", item.getId());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("name", item.getName());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("available", item.getAvailable());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("description",
                                    item.getDescription());
                        }
                );
    }

    @Test
    void getItemWithBookingTest() {
        addItem();
        addBooking();
        List<Comment> comments = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        booking.setId(2L);
        bookings.add(booking);

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(commentRepository.findAllByItem(any()))
                .thenReturn(comments);

        Optional<ItemDTOWithBookings> itemDto = Optional.ofNullable(itemService.get(userOwner.getId(), item.getId()));

        assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(addItemTest -> {
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("id", item.getId());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("name", item.getName());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("available", item.getAvailable());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("description",
                                    item.getDescription());
                        }
                );
    }

    @Test
    void getNotFoundTest() {
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.get(1L, 5L));

        Assertions.assertEquals("Item not found", exception.getMessage());

    }

    @Test
    void getAllOwnItemsTest() {
        addItem();
        addBooking();
        List<Booking> bookings = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        items.add(item);
        bookings.add(booking);
        booking.setId(2L);
        bookings.add(booking);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userOwner));

        Mockito
                .when(itemRepository.findByOwner(any()))
                .thenReturn(items);

        List<ItemDTOWithBookings> getItems = itemService.getAllByOwner(1L, null, null);

        Assertions.assertEquals(getItems.get(0).getId(), items.get(0).getId());
    }

    @Test
    void getAllOwnItemsNotFoundUserTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getAllByOwner(4L, null, null));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getAllOwnItemsFromOrSizeLessThanZeroTest() {
        addItem();

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userOwner));

        final ArithmeticException exception = Assertions.assertThrows(
                ArithmeticException.class,
                () -> itemService.getAllByOwner(1L, -1, 0));

        Assertions.assertEquals("/ by zero",
                exception.getMessage());
    }

    @Test
    void getAllOwnItemsSizeEqualToZeroTest() {
        addItem();

        List<Item> items = new ArrayList<>();
        items.add(item);


        Mockito.when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable((userOwner)));

        log.info(userOwner.toString());
        log.info(String.valueOf(userRepository.getReferenceById(1L)));
        log.info(item.getOwner().toString());

        final ArithmeticException exception = Assertions.assertThrows(
                ArithmeticException.class,
                () -> itemService.getAllByOwner(1L, 1, 0));

        Assertions.assertEquals("/ by zero", exception.getMessage());
    }

    @Test
    void getAllOwnItemsWithPageTest() {
        addItem();
        addBooking();
        List<Booking> bookings = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        items.add(item);
        bookings.add(booking);
        booking.setId(2L);
        bookings.add(booking);
        Page<Item> page = new PageImpl<>(items);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userOwner));

        Mockito
                .when(itemRepository.findByOwner(any(), any()))
                .thenReturn(page);

        List<ItemDTOWithBookings> getItems = itemService.getAllByOwner(1L, 0, 1);

        Assertions.assertEquals(getItems.get(0).getId(), items.get(0).getId());
    }


    @Test
    void getItemsForRentTest() {
        addItem();
        List<Item> items = new ArrayList<>();
        items.add(item);

        Mockito
                .when(itemRepository.findItemsByNameOrDescription(Mockito.anyString()))
                .thenReturn(items);

        List<ItemDTO> getItems = itemService.getForRent("Sword", null, null);

        Assertions.assertEquals(getItems.get(0).getId(), items.get(0).getId());
    }

    @Test
    void getItemsForRentEqualToZeroTest() {
        final ArithmeticException exception = Assertions.assertThrows(
                ArithmeticException.class,
                () -> itemService.getForRent("S", 0, 0));

        Assertions.assertEquals("/ by zero", exception.getMessage());

    }

    @Test
    void getItemsForRentFromOrSizeLessThanZeroTest() {
        final ArithmeticException exception = Assertions.assertThrows(
                ArithmeticException.class,
                () -> itemService.getForRent("S", -1, 0));

        Assertions.assertEquals("/ by zero",
                exception.getMessage());

    }

    @Test
    void getItemsForRentWithPageTest() {
        addItem();
        List<Item> items = new ArrayList<>();
        items.add(item);
        Page<Item> page = new PageImpl<>(items);

        Mockito
                .when(itemRepository.findItemsByNameOrDescription(Mockito.anyString(), any()))
                .thenReturn(page);

        List<ItemDTO> getItems = itemService.getForRent("Sword", 0, 1);

        Assertions.assertEquals(getItems.get(0).getId(), items.get(0).getId());
    }

    @Test
    void getItemsForRentNewArrayTest() {
        List<ItemDTO> getItems = itemService.getForRent("", null, null);

        Assertions.assertEquals(getItems, new ArrayList<>());
    }

    @Test
    void addCommentTest() {
        addItem();
        addComment();
        addBooking();
        booking.setStart(LocalDateTime.now().minusDays(3));
        booking.setEnd(LocalDateTime.now().minusDays(2));
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        CommentDTO dtoWithComment = addItemDtoWithComment();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(comment.getAuthor()));

        Mockito
                .when(bookingRepository.findByItemAndBookerAndStartBeforeAndEndBefore(any(), any(),
                        any(), any()))
                .thenReturn(bookings);

        Mockito
                .when(commentRepository.save(any()))
                .thenReturn(comment);

        Optional<CommentDTO> itemDto = Optional.ofNullable(itemService.addComment(3L, item.getId(),
                dtoWithComment));

        assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(addItemTest -> {
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("id", item.getId());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("text", comment.getText());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("itemName", item.getName());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("created", comment.getCreated());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("authorName",
                                    comment.getAuthor().getName());
                        }
                );
    }

    @Test
    void addCommentItemNotFoundTest() {
        addItem();
        addComment();
        CommentDTO dtoWithComment = addItemDtoWithComment();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(2L, 2L, dtoWithComment));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void addCommentUserNotFoundTest() {
        addItem();
        addComment();
        CommentDTO dtoWithComment = addItemDtoWithComment();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(4L, 1L, dtoWithComment));

        Assertions.assertEquals("User not found", exception.getMessage());

    }

    @Test
    void addCommentBookingIsEmptyTest() {
        addItem();
        addComment();
        CommentDTO dtoWithComment = addItemDtoWithComment();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(comment.getAuthor()));

        Mockito
                .when(bookingRepository.findByItemAndBookerAndStartBeforeAndEndBefore(any(), any(),
                        any(), any()))
                .thenReturn(new ArrayList<>());

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.addComment(4L, 1L, dtoWithComment));

        Assertions.assertEquals("Booking is empty", exception.getMessage());


    }
    
    private void addItem() {
        addUser();
        item.setId(1L);
        item.setName("Sword");
        item.setOwner(userOwner);
        item.setAvailable(true);
        item.setDescription("For fight");
    }

    private void addUser() {
        userOwner.setId(1L);
        userOwner.setName("Aelin");
        userOwner.setEmail("aelin@whitethorn.com");

        requester.setId(2L);
        requester.setName("Rowan");
        requester.setEmail("leo@angel.com");
    }

    private void addRequest() {
        request.setId(1L);
        request.setRequester(requester);
        request.setDescription("waiting for fight");
        request.setCreated(LocalDateTime.now());
    }

    private void addBooking() {
        User booker = new User();
        booker.setId(3L);
        booker.setName("Dorin");
        booker.setEmail("dorin@havilliard.com");
        booking.setId(1L);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setBooker(booker);
    }

    private void addComment() {
        User booker = new User();
        booker.setId(3L);
        booker.setName("Dorin");
        booker.setEmail("dorin@havilliard.com");
        comment.setId(1L);
        comment.setAuthor(booker);
        comment.setItem(item);
        comment.setText("amazing sword");
        comment.setCreated(LocalDateTime.now());
    }

    private CommentDTO addItemDtoWithComment() {
        CommentDTO dtoWithComment = new CommentDTO();
        dtoWithComment.setId(item.getId());
        dtoWithComment.setText(comment.getText());
        dtoWithComment.setItemName(item.getName());
        dtoWithComment.setCreated(comment.getCreated());
        dtoWithComment.setAuthorName(comment.getAuthor().getName());
        return dtoWithComment;
    }

}