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
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.request.dto.RequestDTO;
import ru.practicum.shareit.request.dto.RequestDTOWithItems;
import ru.practicum.shareit.request.service.RequestService;
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

    private final RequestDTO requestDto = new RequestDTO();
    private final RequestDTOWithItems requestWithItems = new RequestDTOWithItems();
    private final ItemDTO itemDto = new ItemDTO();
    private final User user = new User();
    @Autowired
    ObjectMapper mapper;
    @MockBean
    RequestService requestService;
    @Autowired
    private MockMvc mvc;

    @Test
    void addRequestControllerTest() throws Exception {
        addRequestDto();

        when(requestService.add(Mockito.anyLong(), any()))
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
                .andExpect(jsonPath("$.requester.id", is(requestDto.getRequester().getId().intValue())))
                .andExpect(jsonPath("$.requester.name", is(requestDto.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(requestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    @Test
    void getAllOwnRequestControllerTest() throws Exception {
        addRequest();

        when(requestService.findAllByOwner(Mockito.anyLong()))
                .thenReturn(List.of(requestWithItems));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$[0].created", is(requestWithItems.getCreated().toString())))
                .andExpect(jsonPath("$[0].requester.id", is(requestWithItems.getRequester().getId().intValue())))
                .andExpect(jsonPath("$[0].requester.name", is(requestWithItems.getRequester().getName())))
                .andExpect(jsonPath("$[0].requester.email", is(requestWithItems.getRequester().getEmail())))
                .andExpect(jsonPath("$[0].description", is(requestWithItems.getDescription())));
    }

    @Test
    void getAllRequestControllerTest() throws Exception {
        addRequest();

        when(requestService.findAll(Mockito.anyLong(), any(), any()))
                .thenReturn(List.of(requestWithItems));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2L)
                        .param("from", "0")
                        .param("size", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$[0].created", is(requestWithItems.getCreated().toString())))
                .andExpect(jsonPath("$[0].requester.id", is(requestWithItems.getRequester().getId().intValue())))
                .andExpect(jsonPath("$[0].requester.name", is(requestWithItems.getRequester().getName())))
                .andExpect(jsonPath("$[0].requester.email", is(requestWithItems.getRequester().getEmail())))
                .andExpect(jsonPath("$[0].description", is(requestWithItems.getDescription())));
    }

    @Test
    void getRequestControllerTest() throws Exception {
        addRequest();

        when(requestService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(requestWithItems);

        mvc.perform(get("/requests/2")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.created", is(requestWithItems.getCreated().toString())))
                .andExpect(jsonPath("$.requester.id", is(requestWithItems.getRequester().getId().intValue())))
                .andExpect(jsonPath("$.requester.name", is(requestWithItems.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(requestWithItems.getRequester().getEmail())))
                .andExpect(jsonPath("$.description", is(requestWithItems.getDescription())));
    }

    private void addUser() {
        user.setId(1L);
        user.setName("Aelin");
        user.setEmail("aelin@whitethorn.com");
    }

    private void addRequestDto() {
        addUser();
        requestDto.setId(1L);
        requestDto.setDescription("waiting for fight");
        requestDto.setRequester(user);
        String date = "2022-11-23T12:30:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        requestDto.setCreated(localdatetime);
    }

    private void addRequest() {
        addUser();
        user.setId(1L);
        user.setName("Rowan");
        requestWithItems.setId(2L);
        requestWithItems.setDescription("waiting for fight");
        requestWithItems.setRequester(user);
        String date = "2022-11-24T12:30:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        requestWithItems.setCreated(localdatetime);
    }

}