package ru.practicum.shareit.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.user.dto.UserDTO;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String text;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ItemDTO item;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDTO author;
    private String authorName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime created;
}