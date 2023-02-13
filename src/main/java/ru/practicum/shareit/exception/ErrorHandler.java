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
public class ErrorHandler {

    @ExceptionHandler({HttpMessageConversionException.class, BadRequestException.class})
    public ResponseEntity<Map<String, String>> handleValid(final RuntimeException e) {
        log.error("Validation error{}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("Ошибка в валидации", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleConstraintViolation(final ConstraintViolationException e) {
        log.error("Not valid argument{}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("Ошибка в валидации", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidation(final MethodArgumentNotValidException e) {
        log.error("Not valid argument{}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("Ошибка в валидации", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotFound(final NotFoundException e) {
        log.error("Not found{}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("Объект не найден", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleStatus(final StatusBadRequestException e) {
        log.error("Bad request{}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleForbiddenError(final ForbiddenException e) {
        log.error("No rights{}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("Отказано в доступе", e.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleInternalServerError(final Exception e) {
        log.error("Server error{}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("Серверу не удается обработать запрос", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleConflictError(final ConflictException e) {
        log.error("Conflict!{}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("Запрос не может быть выполнен из-за конфликтного обращения к ресурсу", e.getMessage()),
                HttpStatus.CONFLICT
        );
    }
}
