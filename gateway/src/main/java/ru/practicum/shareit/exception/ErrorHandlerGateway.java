package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandlerGateway {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleStatus(final UnknownStateException e) {
        log.warn("Введен неверный статус", e);
        return new ResponseEntity<>(
                Map.of("error", "Unknown state: " + e.getWrongState()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidation(final MethodArgumentNotValidException e) {
        log.warn("Сервер обнаружил в запросе клиента синтаксическую ошибку", e);
        return new ResponseEntity<>(
                Map.of("Ошибка в валидации", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
}
