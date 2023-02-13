package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoForItemTest {
    @Autowired
    private JacksonTester<BookingDTOForItem> json;

    @Test
    void testBookingDto() throws Exception {
        BookingDTOForItem bookingDto = new BookingDTOForItem();
        bookingDto.setId(1);
        bookingDto.setBookerId(1L);
        bookingDto.setDateTime(LocalDateTime.parse("2017-10-19T23:50:50"));

        JsonContent<BookingDTOForItem> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.dateTime").isEqualTo("2017-10-19T23:50:50");
        assertThat(result).extractingJsonPathValue("$.bookerId").isEqualTo(1);
    }

}