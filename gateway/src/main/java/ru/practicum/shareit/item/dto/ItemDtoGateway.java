package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoGateway;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ItemDtoGateway {

    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private User owner;

    private Long requestId;

    private BookingDtoGateway nextBooking;

    private BookingDtoGateway lastBooking;

    private List<CommentDtoGateway> comments;

    @Data
    public static class User {
        private final long id;
        private final String name;
        private final String email;
    }
}
