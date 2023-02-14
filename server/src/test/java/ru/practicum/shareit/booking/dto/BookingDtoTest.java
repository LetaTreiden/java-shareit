package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDTO> json;

    @Test
    void testBookingDto() throws Exception {
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setId(1);
        bookingDto.setItemName("Sword");
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setStart(localdatetime);
        localdatetime = LocalDateTime.parse(date);
        bookingDto.setEnd(localdatetime);
        bookingDto.setBookerId(1L);

        JsonContent<BookingDTO> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.itemName").isEqualTo("Sword");
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo("2017-10-19T23:50:50");
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo("2017-10-19T23:50:50");
    }

}