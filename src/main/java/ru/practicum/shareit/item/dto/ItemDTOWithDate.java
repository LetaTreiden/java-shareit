package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDTOForItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemDTOWithDate {
    @NotNull
    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private User owner;

    private Long request;

    private BookingDTOForItem nextBooking;

    private BookingDTOForItem lastBooking;

    private List<CommentDTO> comments;

    @Data
    public static class User {
        private final long id;
        private final String name;
    }
}
