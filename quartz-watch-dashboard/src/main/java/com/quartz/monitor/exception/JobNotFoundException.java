package com.quartz.monitor.exception;

/**
 * Exception thrown when a job is not found
 */
public class JobNotFoundException extends RuntimeException {
    
    public JobNotFoundException(String message) {
        super(message);
    }
    
    public JobNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 