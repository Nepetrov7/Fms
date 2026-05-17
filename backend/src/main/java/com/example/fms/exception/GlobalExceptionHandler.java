package com.example.fms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Централизованно преобразует типовые исключения backend API в JSON-ответы.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает неверный логин или пароль при входе.
     *
     * @param e исключение аутентификации
     * @return JSON с полем {@code message} и HTTP 401
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage() != null ? e.getMessage() : "Неверный логин или пароль");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Обрабатывает ошибки бизнес-валидации.
     *
     * @param e исключение с текстом ошибки для пользователя
     * @return JSON с полем {@code message} и HTTP 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Обрабатывает ситуации некорректного состояния, например отсутствие авторизации.
     *
     * @param e исключение состояния приложения
     * @return JSON с полем {@code message} и HTTP 401
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Обрабатывает ошибки Bean Validation на DTO запросов.
     *
     * @param ex исключение с привязкой ошибок к полям
     * @return карта {@code имяПоля -> сообщение} и HTTP 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
