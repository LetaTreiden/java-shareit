package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemDtoWithComment;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestServiceImpl requestService;

    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto requestDto = new ItemRequestDto();
    private final RequestDto request = new RequestDto();
    private final ItemDto itemDto = new ItemDto();
    private final Item item = new Item();
    private final User user = new User();
    private final Comment comment = new Comment();
    private final ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking();
    private final BookingDtoForItem booking = new BookingDtoForItem();

    @Test
    void addRequestControllerTest() throws Exception {
        addRequestDto();

        when(requestService.addRequest(Mockito.anyLong(), any()))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().toString())))
                .andExpect(jsonPath("$.requestor.id", is((int) requestDto.getRequestor().getId())))
                .andExpect(jsonPath("$.requestor.name", is(requestDto.getRequestor().getName())))
                .andExpect(jsonPath("$.requestor.email", is(requestDto.getRequestor().getEmail())))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    @Test
    void getAllOwnRequestControllerTest() throws Exception {
        addRequest();

        when(requestService.findAllOwnRequest(Mockito.anyLong()))
                .thenReturn(List.of(request));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$[0].created", is(request.getCreated().toString())))
                .andExpect(jsonPath("$[0].requestor.id", is((int) request.getRequestor().getId())))
                .andExpect(jsonPath("$[0].requestor.name", is(request.getRequestor().getName())))
                .andExpect(jsonPath("$[0].requestor.email", is(request.getRequestor().getEmail())))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())));
    }

    @Test
    void getAllRequestControllerTest() throws Exception {
        addRequest();

        when(requestService.findAllRequest(Mockito.anyLong(), any(), any()))
                .thenReturn(List.of(request));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2L)
                        .param("from", "0")
                        .param("size", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$[0].created", is(request.getCreated().toString())))
                .andExpect(jsonPath("$[0].requestor.id", is((int) request.getRequestor().getId())))
                .andExpect(jsonPath("$[0].requestor.name", is(request.getRequestor().getName())))
                .andExpect(jsonPath("$[0].requestor.email", is(request.getRequestor().getEmail())))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())));
    }

    @Test
    void getRequestControllerTest() throws Exception {
        addRequest();

        when(requestService.findRequest(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(request);

        mvc.perform(get("/requests/2")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.created", is(request.getCreated().toString())))
                .andExpect(jsonPath("$.requestor.id", is((int) request.getRequestor().getId())))
                .andExpect(jsonPath("$.requestor.name", is(request.getRequestor().getName())))
                .andExpect(jsonPath("$.requestor.email", is(request.getRequestor().getEmail())))
                .andExpect(jsonPath("$.description", is(request.getDescription())));
    }

    private void addUser() {
        user.setId(1L);
        user.setName("Buffy");
        user.setEmail("buffy@vampire.com");
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

    private void addRequestDto() {
        addUser();
        requestDto.setId(1L);
        requestDto.setDescription("I need a fork");
        requestDto.setRequestor(user);
        String date = "2022-11-23T12:30:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        requestDto.setCreated(localdatetime);
    }

    private void addRequest() {
        addUser();
        user.setId(1L);
        user.setName("Cat");
        request.setId(2L);
        request.setDescription("I need a fork");
        request.setRequestor(user);
        String date = "2022-11-24T12:30:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        request.setCreated(localdatetime);
    }

}