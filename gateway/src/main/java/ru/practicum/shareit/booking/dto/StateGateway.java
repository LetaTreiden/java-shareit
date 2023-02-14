package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.UnknownStateException;

public enum StateGateway {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    REJECTED,
    WAITING;

    public static StateGateway from(String stringState) {
        for (StateGateway stateGateway : values()) {
            if (stateGateway.name().equalsIgnoreCase(stringState)) {
                return stateGateway;
            }
        }

        throw new UnknownStateException(stringState);
    }
}
