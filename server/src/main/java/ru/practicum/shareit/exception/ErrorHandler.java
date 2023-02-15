package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({HttpMessageConversionException.class, BadRequestException.class})
    public ResponseEntity<Map<String, String>> handleValid(final RuntimeException e) {
        log.warn("Сервер обнаружил в запросе клиента синтаксическую ошибку", e);
        return new ResponseEntity<>(
                Map.of("Ошибка в валидации", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidation(final MethodArgumentNotValidException e) {
        log.warn("Сервер обнаружил в запросе клиента синтаксическую ошибку", e);
        return new ResponseEntity<>(
                Map.of("Ошибка в валидации", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotFound(final NotFoundException e) {
        log.warn("Сервер не обнаружил ресурс", e);
        return new ResponseEntity<>(
                Map.of("Объект не найден", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleStatus(final StatusBadRequestException e) {
        log.warn("Введен неверный статус", e);
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleForbiddenError(final ForbiddenException e) {
        log.warn("Отсутствует доступ для клиента к указанному ресурсу", e);
        return new ResponseEntity<>(
                Map.of("Отказано в доступе", e.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleInternalServerError(final Exception e) {
        log.error("Внутренняя ошибка сервера. Серверу не удалется обработать запрос", e);
        return new ResponseEntity<>(
                Map.of("Серверу не удается обработать запрос", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
