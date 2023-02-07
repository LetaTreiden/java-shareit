package ru.practicum.shareit.item.model;

public interface ItemWithBookings {
    Long getId();

    String getName();

    String getDescription();

    Boolean getAvailable();

    Long getLastBookingId();

    Long getLastBookerId();

    Long getNextBookingId();

    Long getNextBookerId();
}
