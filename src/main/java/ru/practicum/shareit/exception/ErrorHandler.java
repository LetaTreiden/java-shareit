package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({HttpMessageConversionException.class, BadRequestException.class})
    public ResponseEntity<Map<String, String>> handleValid(final RuntimeException e) {
        return new ResponseEntity<>(
                Map.of("Ошибка в валидации", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidation(final MethodArgumentNotValidException e) {
        return new ResponseEntity<>(
                Map.of("Ошибка в валидации", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotFound(final NotFoundException e) {
        return new ResponseEntity<>(
                Map.of("Объект не найден", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleStatus(final StatusBadRequestException e) {
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleForbiddenError(final ForbiddenException e) {
        return new ResponseEntity<>(
                Map.of("Отказано в доступе", e.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleInternalServerError(final Exception e) {
        return new ResponseEntity<>(
                Map.of("Серверу не удается обработать запрос", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleConflictError(final ConflictException e) {
        return new ResponseEntity<>(
                Map.of("Запрос не может быть выполнен из-за конфликтного обращения к ресурсу", e.getMessage()),
                HttpStatus.CONFLICT
        );
    }
}
