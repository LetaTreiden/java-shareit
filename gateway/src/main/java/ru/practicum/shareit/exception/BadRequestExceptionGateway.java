package ru.practicum.shareit.exception;

public class BadRequestExceptionGateway extends IllegalArgumentException {
        public BadRequestExceptionGateway(String message) {
            super(message);
        }
    }

