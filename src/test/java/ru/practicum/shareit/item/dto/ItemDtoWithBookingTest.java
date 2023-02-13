package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDTOForItem;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoWithBookingTest {

    private final User user = new User();
    private final BookingDTOForItem last = new BookingDTOForItem();
    private final BookingDTOForItem next = new BookingDTOForItem();
    @Autowired
    private JacksonTester<ItemDTOWithBookings> json;

    @Test
    void testItemDtoWithBooking() throws IOException {
        addUser();
        addLast();
        addNext();
        ItemDTOWithBookings itemDtoWithBooking = new ItemDTOWithBookings();
        itemDtoWithBooking.setId(1);
        itemDtoWithBooking.setName("Sword");
        itemDtoWithBooking.setDescription("To fight");
        itemDtoWithBooking.setOwner(UserMapper.toUserToItemWithBookingsDto(user));
        itemDtoWithBooking.setRequest(1L);
        itemDtoWithBooking.setLastBooking(last);
        itemDtoWithBooking.setNextBooking(next);
        itemDtoWithBooking.setAvailable(true);

        JsonContent<ItemDTOWithBookings> result = json.write(itemDtoWithBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Sword");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo("Aelin");
        assertThat(result).extractingJsonPathStringValue("$.owner.email")
                .isEqualTo("aelin@whitethorn.com");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("To fight");
        assertThat(result).extractingJsonPathValue("$.lastBooking.dateTime")
                .isEqualTo("2017-10-19T23:50:50");
        assertThat(result).extractingJsonPathValue("$.nextBooking.dateTime")
                .isEqualTo("2017-10-19T23:50:50");
        assertThat(result).extractingJsonPathNumberValue("$.request").isEqualTo(1);

    }

    private void addUser() {
        user.setId(1L);
        user.setName("Aelin");
        user.setEmail("aelin@whitethorn.com");
    }

    private void addLast() {
        User userForLast = new User();
        userForLast.setId(2L);
        userForLast.setName("Rowan");
        userForLast.setEmail("rowan@whitethorn.com");
        last.setId(1);
        last.setBookerId(userForLast.getId());
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        last.setDateTime(localdatetime);
    }

    private void addNext() {
        User userForNext = new User();
        userForNext.setId(3L);
        userForNext.setName("Rowan");
        userForNext.setEmail("rowan@whitethorn.com");
        next.setId(2);
        next.setBookerId(userForNext.getId());
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        next.setDateTime(localdatetime);
    }

}