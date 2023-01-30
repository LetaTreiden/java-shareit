package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingToItem;
import ru.practicum.shareit.comment.CommentDTO;
import ru.practicum.shareit.user.dto.UserDTO;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @NotNull
    @JsonProperty(value = "available")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isAvailable;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserDTO owner;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long requestId;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private BookingToItem lastBooking;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private BookingToItem nextBooking;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<CommentDTO> comments = new HashSet<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private Long id;
        private String name;
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Booking {
        private Long id;
        private Long bookerId;
        private LocalDateTime start;
        private LocalDateTime end;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Comment {
        Long id;
        String text;
        String authorName;
    }

    @Data
    @AllArgsConstructor
    public static class BookingIner {
        private Long id;
        private Long bookerId;
    }
}