package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationException extends RuntimeException {
    public ValidationException(String message, String className) {
        super(message);
        log.error("{}. {}", className, message);
    }
}