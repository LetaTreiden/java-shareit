package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StatusBadRequestException extends RuntimeException {
    public StatusBadRequestException(String s) {
        super(s);
    }
}
