package com.techtorque.time_logging_service.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest req) {
        Map<String, Object> body = new HashMap<>();
        int statusCode = ex.getStatusCode().value();
        HttpStatus httpStatus = HttpStatus.resolve(statusCode);
        String reason = httpStatus != null ? httpStatus.getReasonPhrase() : ex.getStatusCode().toString();
        body.put("timestamp", Instant.now().toString());
        body.put("status", statusCode);
        body.put("error", reason);
        body.put("message", ex.getReason());
        body.put("path", req.getRequestURI());
        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("message", ex.getBindingResult().getFieldErrors().stream().map(f -> f.getField() + ": " + f.getDefaultMessage()).findFirst().orElse(ex.getMessage()));
        body.put("path", req.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, HttpServletRequest req) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", req.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
