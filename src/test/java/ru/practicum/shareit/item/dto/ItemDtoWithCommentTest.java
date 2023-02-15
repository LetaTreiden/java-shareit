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
    private JacksonTester<CommentDTO> json;

    @Test
    void testItemDtoWithComment() throws Exception {
        String date = "2017-10-19T23:50:50";
        LocalDateTime localdatetime = LocalDateTime.parse(date);
        CommentDTO itemDto = new CommentDTO();
        itemDto.setId(1L);
        itemDto.setItemName("Sword");
        itemDto.setText("Waiting for fight");
        itemDto.setCreated(localdatetime);
        itemDto.setAuthorName("Rowan");

        JsonContent<CommentDTO> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.itemName").isEqualTo("Sword");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Waiting for fight");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Rowan");
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo("2017-10-19T23:50:50");

    }
}