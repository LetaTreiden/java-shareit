package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDTO> json;

    private final User user = new User();

    @Test
    void testItemDto() throws Exception {
        addUser();
        ItemDTO itemDto = new ItemDTO();
        itemDto.setId(1L);
        itemDto.setName("Sword");
        itemDto.setAvailable(true);
        itemDto.setDescription("To fight");
        itemDto.setOwner(UserMapper.toUserToItemDto(user));
        itemDto.setRequestId(1L);

        JsonContent<ItemDTO> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Sword");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo("Aelin");
        assertThat(result).extractingJsonPathStringValue("$.owner.email")
                .isEqualTo("aelin@whitethorn.com");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("To fight");
        assertThat(result).extractingJsonPathNumberValue("$.request").isEqualTo(1);
    }

    private void addUser() {
        user.setId(1L);
        user.setName("Aelin");
        user.setEmail("aelin@whitethorn.com");
    }
}