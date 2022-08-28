package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message, String className) {
        super(message);
        log.error("{}. {}", className, message);
    }
}