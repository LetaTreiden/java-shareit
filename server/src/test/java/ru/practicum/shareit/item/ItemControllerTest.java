package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemDtoWithComment;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemServiceImpl itemService;

    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = new ItemDto();
    private final Item item = new Item();
    private final User user = new User();
    private final Comment comment = new Comment();
    private final ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking();
    private final BookingDtoForItem booking = new BookingDtoForItem();

    @Test
    void addItemControllerTest() throws Exception {
        addItemDto();

        when(itemService.addItem(Mockito.anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.owner.id", is((int) itemDto.getOwner().getId())))
                .andExpect(jsonPath("$.owner.name", is(itemDto.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(itemDto.getOwner().getEmail())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void changeItemControllerTest() throws Exception {
        addItemDto();

        when(itemService.changeItem(Mockito.anyLong(), Mockito.anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1/")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.owner.id", is((int) itemDto.getOwner().getId())))
                .andExpect(jsonPath("$.owner.name", is(itemDto.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(itemDto.getOwner().getEmail())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getItemControllerTest() throws Exception {
        addItemDtoWithBooking();

        when(itemService.getItem(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemDtoWithBooking);

        mvc.perform(get("/items/1/")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBooking.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithBooking.getName())))
                .andExpect(jsonPath("$.owner.id", is((int) itemDtoWithBooking.getOwner().getId())))
                .andExpect(jsonPath("$.owner.name", is(itemDtoWithBooking.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(itemDtoWithBooking.getOwner().getEmail())))
                .andExpect(jsonPath("$.lastBooking.id", is((int) itemDtoWithBooking.getLastBooking().getId())))
                .andExpect(jsonPath("$.lastBooking.bookerId", is((int) itemDtoWithBooking.getLastBooking()
                        .getBookerId())))
                .andExpect(jsonPath("$.lastBooking.dateTime", is(itemDtoWithBooking.getLastBooking()
                        .getDateTime().toString())))
                .andExpect(jsonPath("$.nextBooking.id", is((int) itemDtoWithBooking.getNextBooking().getId())))
                .andExpect(jsonPath("$.nextBooking.bookerId", is((int) itemDtoWithBooking.getNextBooking()
                        .getBookerId())))
                .andExpect(jsonPath("$.nextBooking.dateTime", is(itemDtoWithBooking.getNextBooking()
                        .getDateTime().toString())))
                .andExpect(jsonPath("$.description", is(itemDtoWithBooking.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithBooking.getAvailable())));
    }

    @Test
    void getAllOwnItemsControllerTest() throws Exception {
        addItemDtoWithBooking();
        List<ItemDtoWithBooking> items = new ArrayList<>();
        items.add(itemDtoWithBooking);

        when(itemService.getAllOwnItems(Mockito.anyLong(), any(), any()))
                .thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoWithBooking.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithBooking.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoWithBooking.getDescription())))
                .andExpect(jsonPath("$[0].owner.id", is((int) itemDtoWithBooking.getOwner().getId())))
                .andExpect(jsonPath("$[0].owner.name", is(itemDtoWithBooking.getOwner().getName())))
                .andExpect(jsonPath("$[0].owner.email", is(itemDtoWithBooking.getOwner().getEmail())))
                .andExpect(jsonPath("$[0].lastBooking.id", is((int) itemDtoWithBooking.getLastBooking()
                        .getId())))
                .andExpect(jsonPath("$[0].lastBooking.bookerId", is((int) itemDtoWithBooking.getLastBooking()
                        .getBookerId())))
                .andExpect(jsonPath("$[0].lastBooking.dateTime", is(itemDtoWithBooking.getLastBooking()
                        .getDateTime().toString())))
                .andExpect(jsonPath("$[0].nextBooking.id", is((int) itemDtoWithBooking.getNextBooking().getId())))
                .andExpect(jsonPath("$[0].nextBooking.bookerId", is((int) itemDtoWithBooking.getNextBooking()
                        .getBookerId())))
                .andExpect(jsonPath("$[0].nextBooking.dateTime", is(itemDtoWithBooking.getNextBooking()
                        .getDateTime().toString())))
                .andExpect(jsonPath("$[0].available", is(itemDtoWithBooking.getAvailable())));

    }

    @Test
    void getItemsForRentControllerTest() throws Exception {
        addItemDto();
        when(itemService.getItemsForRent(Mockito.anyString(), any(), any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=F")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].owner.id", is((int) itemDto.getOwner().getId())))
                .andExpect(jsonPath("$[0].owner.name", is(itemDto.getOwner().getName())))
                .andExpect(jsonPath("$[0].owner.email", is(itemDto.getOwner().getEmail())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void addCommentControllerTest() throws Exception {
        addItemDto();
        addComment();
        ItemDtoWithComment item = addItemDtoWithComment();
        when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), any(ItemDtoWithComment.class)))
                .thenReturn(item);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(item.getText())))
                .andExpect(jsonPath("$.itemName", is(item.getItemName())))
                .andExpect(jsonPath("$.created", is(item.getCreated().toString())))
                .andExpect(jsonPath("$.authorName", is(item.getAuthorName())));

    }

    @Test
    void updateItemWithException() throws Exception {
        when(itemService.changeItem(5L, 5L, new ItemDto()))
                .thenThrow(AssertionError.class);

        mvc.perform(patch("/items/10/")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }

    private void addItemDto() {
        addUser();
        itemDto.setId(1L);
        itemDto.setName("Fork");
        itemDto.setOwner(user);
        itemDto.setAvailable(true);
        itemDto.setDescription("Designed for food");
    }

    private void addItemDtoWithBooking() {
        addBooking();
        addUser();
        itemDtoWithBooking.setId(2L);
        itemDtoWithBooking.setName("Fork");
        itemDtoWithBooking.setLastBooking(booking);
        booking.setId(2L);
        booking.setBookerId(4L);
        String date = "2022-11-23T12:30:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setDateTime(localdatetime);
        booking.setBookerId(4L);
        itemDtoWithBooking.setNextBooking(booking);
        itemDtoWithBooking.setOwner(user);
        itemDtoWithBooking.setAvailable(true);
        itemDtoWithBooking.setDescription("Designed for food");
    }

    private void addUser() {
        user.setId(1L);
        user.setName("Buffy");
        user.setEmail("buffy@vampire.com");
    }

    private void addBooking() {
        User booker = new User();
        booker.setId(3L);
        booker.setName("Katya");
        booker.setEmail("katya@katya.com");
        booking.setId(1L);
        booking.setBookerId(3L);
        String date = "2022-11-22T12:30:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        booking.setDateTime(localdatetime);
        User booker2 = new User();
        booker2.setId(4L);
        booker2.setName("Kat");
        booker2.setEmail("kat@kat.com");

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
        String date = "2022-11-23T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        comment.setCreated(localdatetime);
    }

    private ItemDtoWithComment addItemDtoWithComment() {
        ItemDtoWithComment dtoWithComment = new ItemDtoWithComment();
        dtoWithComment.setId(itemDto.getId());
        dtoWithComment.setText(comment.getText());
        dtoWithComment.setItemName(itemDto.getName());
        dtoWithComment.setCreated(comment.getCreated());
        dtoWithComment.setAuthorName(comment.getAuthor().getName());
        return dtoWithComment;
    }
}