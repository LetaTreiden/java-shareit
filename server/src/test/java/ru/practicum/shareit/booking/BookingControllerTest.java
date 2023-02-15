package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDTOToReturn;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private final BookingDTOToReturn bookingDto = new BookingDTOToReturn();
    private final Item item = new Item();
    private final User user = new User();
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingServiceImpl bookingService;
    @Autowired
    private MockMvc mvc;

    @Test
    void addBookingControllerTest() throws Exception {
        addItem();
        addBookingDto();

        when(bookingService.add(Mockito.anyLong(), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 4L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.booker.id", is((int)bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is((int) bookingDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

    }

    @Test
    void updateStatusBookingControllerTest() throws Exception {
        addItem();
        addBookingDto();

        when(bookingService.update(Mockito.any(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/2")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.booker.id", is((int)bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is((int) bookingDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getBookingControllerTest() throws Exception {
        addItem();
        addBookingDto();

        when(bookingService.get(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/2")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.booker.id", is((int)bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is((int) bookingDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void findBookingByBookerControllerTest() throws Exception {
        addItem();
        addBookingDto();
        List<BookingDTOToReturn> bookings = new ArrayList<>();
        bookings.add(bookingDto);

        when(bookingService.getByBooker(Mockito.anyLong(), Mockito.anyString(), any(), any()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 4L)
                        .param("state", "ALL")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].booker.id", is((int)bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is((int) bookingDto.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void findBookingByOwnerControllerTest() throws Exception {
        addItem();
        addBookingDto();
        List<BookingDTOToReturn> bookings = new ArrayList<>();
        bookings.add(bookingDto);

        when(bookingService.getByOwner(Mockito.anyLong(), Mockito.anyString(), any(), any()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].booker.id", is((int)bookingDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is((int) bookingDto.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    private void addItem() {
        addUser();
        item.setId(1L);
        item.setName("Sword");
        item.setOwner(user);
        item.setAvailable(true);
        item.setDescription("For fight");
    }

    private void addUser() {
        user.setId(1L);
        user.setName("Aelin");
        user.setEmail("aelin@whitethorn.com");
    }

    private void addBookingDto() {
        User booker = new User();
        booker.setId(4L);
        booker.setName("Rowan");
        booker.setEmail("rowan@whitethorn.com");
        bookingDto.setId(2L);
        bookingDto.setItem(ItemMapper.toItemToBookingDTO(item));
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setBooker(UserMapper.toUserToBookingDTO(booker));
        String date = "2023-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        bookingDto.setStart(localdatetime);
        date = "2023-11-26T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        bookingDto.setEnd(localdatetime);
    }
}