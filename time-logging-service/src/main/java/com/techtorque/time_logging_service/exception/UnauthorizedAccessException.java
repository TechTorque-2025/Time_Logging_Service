package com.techtorque.time_logging_service.exception;

/**
 * Exception thrown when a user attempts to access or modify a resource
 * they are not authorized to access.
 * 
 * Used for enforcing ownership rules (e.g., employees can only modify their own time logs)
 */
public class UnauthorizedAccessException extends RuntimeException {
    
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
