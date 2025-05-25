package com.quartz.monitor.exception;

/**
 * Exception thrown when a scheduler is not found
 */
public class SchedulerNotFoundException extends RuntimeException {
    
    public SchedulerNotFoundException(String message) {
        super(message);
    }
    
    public SchedulerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 