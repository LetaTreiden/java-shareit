package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.UnknownStateException;

public enum StateGateway {
    // Все
    ALL,
    // Текущие
    CURRENT,
    // Будущие
    FUTURE,
    // Завершенные
    PAST,
    // Отклоненные
    REJECTED,
    // Ожидающие подтверждения
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
