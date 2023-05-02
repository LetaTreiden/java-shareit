package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandlerGateway {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleStatus(final UnknownStateException e) {
        log.warn("Введен неверный статус ", e);
        return new ResponseEntity<>(
                Map.of("error", "Unknown state: " + e.getWrongState()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidation(final MethodArgumentNotValidException e) {
        log.warn("Сервер обнаружил в запросе клиента синтаксическую ошибку ", e);
        return new ResponseEntity<>(
                Map.of("Ошибка в валидации", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({HttpMessageConversionException.class, BadRequestExceptionGateway.class})
    public ResponseEntity<Map<String, String>> handleValid(final RuntimeException e) {
        log.error("Validation error{}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("Ошибка в валидации", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleConstraintViolation(final ConstraintViolationException e) {
        log.error("Not valid argument{} ", e.getMessage());
        return new ResponseEntity<>(
                Map.of("Ошибка в валидации", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleInternalServerError(final Throwable e) {
        log.error("Server error{} ", e.getMessage());
        return new ResponseEntity<>(
                Map.of("Серверу не удается обработать запрос", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
