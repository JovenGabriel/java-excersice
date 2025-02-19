package com.threeit.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation exceptions by constructing a response entity with details of validation errors.
     *
     * @param ex the MethodArgumentNotValidException exception that contains details about validation failures
     * @return a ResponseEntity containing a map with error information and field-specific error messages,
     *         with an HTTP status of Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("error", "Bad Request");

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        errorResponse.put("fieldErrors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles NotFoundException and constructs a response entity with error details.
     *
     * @param ex the NotFoundException thrown when a requested resource is not found
     * @return a ResponseEntity containing a map with error information and a status of 404 Not Found
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Not Found");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles the EmailAlreadyExistsException by returning a response entity
     * with a conflict status and an error message.
     *
     * @param ex the exception thrown when an email already exists
     * @return a ResponseEntity containing the error details and HTTP status
     */
    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Conflict");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}