package ru.practicum.booking;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

  @Data
  @Builder
  @Getter
  @Setter
  public class BookingDTO {
      private Long id;
      private LocalDateTime start;
      private LocalDateTime end;
      private Item item;
      private User booker;
      private User owner;
      private BookingStatus bookingStatus;

      @Data
      @Builder
      @Getter
      @Setter
      public static class Item {
          private Long id;
          private String name;
          private String description;
          private boolean available;
          private String request;
    }

      @Data
      @Builder
      @Getter
      @Setter
      public static class User {
          private Long id;
          private String name;
          private String email;
    }
}