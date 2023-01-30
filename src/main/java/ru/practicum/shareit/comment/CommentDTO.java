package ru.practicum.shareit.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String text;
    private String authorName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime created;
}