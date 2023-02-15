package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoWithCommentTest {

    @Autowired
    private JacksonTester<ItemDtoWithComment> json;

    @Test
    void testItemDtoWithComment() throws Exception {
        String date = "2022-11-23T18:08:54";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        ItemDtoWithComment itemDto = new ItemDtoWithComment();
        itemDto.setId(1L);
        itemDto.setItemName("Fork");
        itemDto.setText("I need a fork");
        itemDto.setCreated(localdatetime);
        itemDto.setAuthorName("Leo");

        JsonContent<ItemDtoWithComment> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.itemName").isEqualTo("Fork");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("I need a fork");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Leo");
        assertThat(result).extractingJsonPathValue("$.created")
                .isEqualTo("2022-11-23T18:08:54");

    }
}