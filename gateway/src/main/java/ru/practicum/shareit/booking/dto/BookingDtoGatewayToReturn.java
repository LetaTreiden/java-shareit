package ru.practicum.shareit.booking.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@Validated

public class BookingDtoGatewayToReturn {

        private long id;

        private LocalDateTime start;

        private LocalDateTime end;

        private Item item;

        private User booker;

        private StateGateway status;

        @Data
        public static class User {
            private final long id;
            private final String name;
        }

        @Data
        public static class Item {
            private final long id;
            private final String name;
        }
}
