package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoToReturnTest {
    @Autowired
    private JacksonTester<BookingDTOToReturn> json;

    private final BookingDTOToReturn.User user = new BookingDTOToReturn.User(2L, "Rowan");
    private final BookingDTOToReturn.Item item = new BookingDTOToReturn.Item(1L, "Sword");

    @Test
    void testBookingDto() throws Exception {
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        BookingDTOToReturn bookingDto = new BookingDTOToReturn();
        bookingDto.setId(1);
        bookingDto.setItem(item);
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setStart(localdatetime);
        localdatetime = LocalDateTime.parse(date);
        bookingDto.setEnd(localdatetime);
        bookingDto.setBooker(user);

        JsonContent<BookingDTOToReturn> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item.name").isEqualTo("Sword");
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo("2017-10-19T23:50:50");
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo("2017-10-19T23:50:50");
        assertThat(result).extractingJsonPathValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathValue("$.booker.name").isEqualTo("Rowan");
    }

}