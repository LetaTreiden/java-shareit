package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.State;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        String date = "2021-11-21T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1);
        bookingDto.setItemName("Fork");
        bookingDto.setStatus(State.WAITING);
        bookingDto.setStart(localdatetime);
        date = "2022-11-21T18:08:54";
        localdatetime = LocalDateTime.parse(date);
        bookingDto.setEnd(localdatetime);
        bookingDto.setBookerId(1L);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.itemName").isEqualTo("Fork");
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo("2021-11-21T18:08:54");
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo("2022-11-21T18:08:54");
    }

}