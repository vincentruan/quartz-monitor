package com.quartz.monitor.exception;

/**
 * Exception thrown when a trigger is not found
 */
public class TriggerNotFoundException extends RuntimeException {
    
    public TriggerNotFoundException(String message) {
        super(message);
    }
    
    public TriggerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 