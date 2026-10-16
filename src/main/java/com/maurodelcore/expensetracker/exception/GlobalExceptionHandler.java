package com.maurodelcore.expensetracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Central place that turns exceptions thrown anywhere in the controllers or services
 * into HTTP responses, so no controller needs its own try/catch for these cases.
 * Spring calls the matching handler method whenever one of these exceptions escapes
 * a controller.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles a sign-up attempt with a username that is already taken.
     * The request is well formed but clashes with existing data, so the status is 409 Conflict.
     *
     * @param e the exception thrown by the service when the username already exists
     * @return a 409 Conflict response whose body is the exception message
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    /**
     * Handles a login attempt with a wrong username or password.
     * The server cannot tell who is asking, so the status is 401 Unauthorized.
     * Spring's message does not say which of the two was wrong.
     *
     * @param e the exception thrown by the authentication manager when the credentials do not match
     * @return a 401 Unauthorized response whose body is the exception message
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    /**
     * Handles a request body that fails a {@code @Valid} check, such as a blank username
     * or an amount that is not above zero. The client sent bad data, so the status is
     * 400 Bad Request. The body maps each invalid field to one message, for example
     * {@code {"amount": "amount must be greater than zero"}}.
     *
     * @param e the exception thrown by Spring when one or more fields of the body are invalid
     * @return a 400 Bad Request response whose body maps each invalid field name to its message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> messagesByField = new LinkedHashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            // Keep the first message if a field breaks more than one rule
            messagesByField.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messagesByField);
    }
}