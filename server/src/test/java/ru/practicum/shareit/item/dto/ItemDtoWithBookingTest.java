package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoWithBookingTest {

    @Autowired
    private JacksonTester<ItemDtoWithBooking> json;

    private final User user = new User();
    private final BookingDtoForItem last = new BookingDtoForItem();
    private final BookingDtoForItem next = new BookingDtoForItem();

    @Test
    void testItemDtoWithBooking() throws IOException {
        addUser();
        addLast();
        addNext();
        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking();
        itemDtoWithBooking.setId(1);
        itemDtoWithBooking.setName("Fork");
        itemDtoWithBooking.setDescription("For food");
        itemDtoWithBooking.setOwner(user);
        itemDtoWithBooking.setRequest(1L);
        itemDtoWithBooking.setLastBooking(last);
        itemDtoWithBooking.setNextBooking(next);
        itemDtoWithBooking.setAvailable(true);

        JsonContent<ItemDtoWithBooking> result = json.write(itemDtoWithBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Fork");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo("Buffy");
        assertThat(result).extractingJsonPathStringValue("$.owner.email")
                .isEqualTo("buffy@vampire.com");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("For food");
        assertThat(result).extractingJsonPathValue("$.lastBooking.dateTime")
                .isEqualTo("2022-11-22T18:08:54");
        assertThat(result).extractingJsonPathValue("$.nextBooking.dateTime")
                .isEqualTo("2022-11-24T18:08:54");
        assertThat(result).extractingJsonPathNumberValue("$.request").isEqualTo(1);

    }

    private void addUser() {
        user.setId(1);
        user.setName("Buffy");
        user.setEmail("buffy@vampire.com");
    }

    private void addLast() {
        User userForLast = new User();
        userForLast.setId(2);
        userForLast.setName("Leo");
        userForLast.setEmail("leo@leo.com");
        last.setId(1);
        last.setBookerId(userForLast.getId());
        String date = "2022-11-22T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        last.setDateTime(localdatetime);
    }

    private void addNext() {
        User userForNext = new User();
        userForNext.setId(3);
        userForNext.setName("Leo");
        userForNext.setEmail("leo@leo.com");
        next.setId(2);
        next.setBookerId(userForNext.getId());
        String date = "2022-11-24T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        next.setDateTime(localdatetime);
    }

}