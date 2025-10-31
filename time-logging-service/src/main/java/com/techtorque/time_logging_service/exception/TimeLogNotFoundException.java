package com.techtorque.time_logging_service.exception;

public class TimeLogNotFoundException extends RuntimeException {
    public TimeLogNotFoundException(String message) {
        super(message);
    }
}
