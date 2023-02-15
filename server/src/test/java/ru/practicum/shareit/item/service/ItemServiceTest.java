package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComment;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
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
class ItemServiceTest {

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRequestRepository requestRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingRepository bookingRepository;

    Item item = new Item();
    User userOwner = new User();
    User requestor = new User();
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

        Optional<ItemDto> itemDto = Optional.ofNullable(itemService.addItem(userOwner.getId(),
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
        item.setRequest(request);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(userOwner));

        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(request));

        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(item);

        Optional<ItemDto> itemDto = Optional.ofNullable(itemService.addItem(userOwner.getId(),
                ItemMapper.toItemDto(item)));

        assertThat(itemDto)
                .isPresent()
                .hasValueSatisfying(addItemTest -> {
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("id", item.getId());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("name", item.getName());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("available", item.getAvailable());
                            assertThat(addItemTest).hasFieldOrPropertyWithValue("requestId",
                                    item.getRequest().getId());
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
                () -> itemService.addItem(3L, ItemMapper.toItemDto(item)));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void changeItemTest() {
        addItem();

        Mockito
                .when(itemRepository.save(any()))
                .thenReturn(item);

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Optional<ItemDto> itemDto = Optional.ofNullable(itemService.changeItem(userOwner.getId(), item.getId(),
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
                () -> itemService.changeItem(1L, 3L, ItemMapper.toItemDto(item)));

        Assertions.assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void changeItemForbiddenExceptionTest() {
        addItem();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        final ForbiddenException exception = Assertions.assertThrows(
                ForbiddenException.class,
                () -> itemService.changeItem(5L, 1L, ItemMapper.toItemDto(item)));

        Assertions.assertEquals("Для пользователя нет доступа", exception.getMessage());
    }

    @Test
    void getItemTest() {
        addItem();
        List<Comment> comments = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(commentRepository.findByItem(any()))
                .thenReturn(comments);

        Mockito
                .when(bookingRepository.findByItemOrderByStartDesc(any()))
                .thenReturn(bookings);

        Optional<ItemDtoWithBooking> itemDto = Optional.ofNullable(itemService.getItem(userOwner.getId(), item.getId()));

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
                .when(commentRepository.findByItem(any()))
                .thenReturn(comments);

        Mockito
                .when(bookingRepository.findByItemOrderByStartDesc(any()))
                .thenReturn(bookings);

        Optional<ItemDtoWithBooking> itemDto = Optional.ofNullable(itemService.getItem(userOwner.getId(), item.getId()));

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
                () -> itemService.getItem(1L, 5L));

        Assertions.assertEquals("Вещь не найдена", exception.getMessage());

    }

    @Test
    void getAllOwnItemsTest() {
        addItem();
        addBooking();
        List<Comment> comments = new ArrayList<>();
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
                .when(commentRepository.findByItem(any()))
                .thenReturn(comments);

        Mockito
                .when(bookingRepository.findByItemOrderByStartDesc(any()))
                .thenReturn(bookings);

        Mockito
                .when(itemRepository.findByOwner(any()))
                .thenReturn(items);

        List<Item> getItems = ItemMapper.mapToItem(itemService.getAllOwnItems(1L, null, null));

        Assertions.assertEquals(getItems.get(0).getId(), items.get(0).getId());
    }

    @Test
    void getAllOwnItemsNotFoundUserTest() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getAllOwnItems(4L, null, null));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }


    @Test
    void getAllOwnItemsWithPageTest() {
        addItem();
        addBooking();
        List<Comment> comments = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        items.add(item);
        bookings.add(booking);
        booking.setId(2L);
        bookings.add(booking);
        Page<Item> page = new PageImpl<>(items);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userOwner));

        Mockito
                .when(commentRepository.findByItem(any()))
                .thenReturn(comments);

        Mockito
                .when(bookingRepository.findByItemOrderByStartDesc(any()))
                .thenReturn(bookings);

        Mockito
                .when(itemRepository.findByOwner(any(), any()))
                .thenReturn(page);

        List<Item> getItems = ItemMapper.mapToItem(itemService.getAllOwnItems(1L, 0, 1));

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

        List<ItemDto> getItems = (List<ItemDto>) itemService.getItemsForRent("Fork", null, null);

        Assertions.assertEquals(getItems.get(0).getId(), items.get(0).getId());
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

        List<ItemDto> getItems = (List<ItemDto>) itemService.getItemsForRent("Fork", 0, 1);

        Assertions.assertEquals(getItems.get(0).getId(), items.get(0).getId());
    }

    @Test
    void getItemsForRentNewArrayTest() {
        List<ItemDto> getItems = (List<ItemDto>) itemService.getItemsForRent("", null, null);

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
        ItemDtoWithComment dtoWithComment = addItemDtoWithComment();

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

        Optional<ItemDtoWithComment> itemDto = Optional.ofNullable(itemService.addComment(3L, item.getId(),
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
        ItemDtoWithComment dtoWithComment = addItemDtoWithComment();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(2L, 2L, dtoWithComment));

        Assertions.assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void addCommentUserNotFoundTest() {
        addItem();
        addComment();
        ItemDtoWithComment dtoWithComment = addItemDtoWithComment();

        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(4L, 1L, dtoWithComment));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());

    }

    @Test
    void addCommentBookingIsEmptyTest() {
        addItem();
        addComment();
        ItemDtoWithComment dtoWithComment = addItemDtoWithComment();

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

        Assertions.assertEquals("Невозможно добавить комментарий", exception.getMessage());


    }

    private void addItem() {
        addUser();
        item.setId(1L);
        item.setName("Fork");
        item.setOwner(userOwner);
        item.setAvailable(true);
        item.setDescription("Designed for food");
    }

    private void addUser() {
        userOwner.setId(1L);
        userOwner.setName("Buffy");
        userOwner.setEmail("buffy@vampire.com");

        requestor.setId(2L);
        requestor.setName("Leo");
        requestor.setEmail("leo@angel.com");
    }

    private void addRequest() {
        request.setId(1L);
        request.setRequestor(requestor);
        request.setDescription("I need a fork to eat");
        request.setCreated(LocalDateTime.now());
    }

    private void addBooking() {
        User booker = new User();
        booker.setId(3L);
        booker.setName("Katya");
        booker.setEmail("katya@katya.com");
        booking.setId(1L);
        booking.setItem(item);
        booking.setStatus(State.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setBooker(booker);
    }

    private void addComment() {
        User booker = new User();
        booker.setId(3L);
        booker.setName("Katya");
        booker.setEmail("katya@katya.com");
        comment.setId(1L);
        comment.setAuthor(booker);
        comment.setItem(item);
        comment.setText("cool fork");
        comment.setCreated(LocalDateTime.now());
    }

    private ItemDtoWithComment addItemDtoWithComment() {
        ItemDtoWithComment dtoWithComment = new ItemDtoWithComment();
        dtoWithComment.setId(item.getId());
        dtoWithComment.setText(comment.getText());
        dtoWithComment.setItemName(item.getName());
        dtoWithComment.setCreated(comment.getCreated());
        dtoWithComment.setAuthorName(comment.getAuthor().getName());
        return dtoWithComment;
    }

}