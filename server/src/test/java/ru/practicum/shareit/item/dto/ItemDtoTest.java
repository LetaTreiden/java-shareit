package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    private final User user = new User();

    @Test
    void testItemDto() throws Exception {
        addUser();
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Fork");
        itemDto.setAvailable(true);
        itemDto.setDescription("For food");
        itemDto.setOwner(user);
        itemDto.setRequestId(1L);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Fork");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo("Buffy");
        assertThat(result).extractingJsonPathStringValue("$.owner.email")
                .isEqualTo("buffy@vampire.com");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("For food");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    private void addUser() {
        user.setId(1L);
        user.setName("Buffy");
        user.setEmail("buffy@vampire.com");
    }
}