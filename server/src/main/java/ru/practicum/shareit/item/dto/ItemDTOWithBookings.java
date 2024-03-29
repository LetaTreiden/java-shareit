package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDTOForItem;

import java.util.Comparator;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemDTOWithBookings {
    private long id;

    private String name;

    private String description;

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
        private final String email;
    }

    public static final Comparator<ItemDTOWithBookings> COMPARE_BY_ID = new Comparator<ItemDTOWithBookings>() {
        @Override
        public int compare(ItemDTOWithBookings lhs, ItemDTOWithBookings rhs) {
            return (int) (lhs.getId() - rhs.getId());
        }
    };
}
