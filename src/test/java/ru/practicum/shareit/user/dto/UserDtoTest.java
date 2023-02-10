package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDTO> json;

    @Test
    void testUserDto() throws Exception {
        UserDTO userDto = new UserDTO();
        userDto.setId(1L);
        userDto.setName("Leo");
        userDto.setEmail("leo@angel.com");

        JsonContent<UserDTO> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Leo");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("leo@angel.com");
    }

}