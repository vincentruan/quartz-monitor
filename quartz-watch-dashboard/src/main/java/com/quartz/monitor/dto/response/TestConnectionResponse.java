package com.quartz.monitor.dto.response;

/**
 * Test connection response DTO
 */
public class TestConnectionResponse {
    
    private int schedulerCount;
    private String message;
    private boolean success;
    
    public TestConnectionResponse() {}
    
    public TestConnectionResponse(int schedulerCount, String message, boolean success) {
        this.schedulerCount = schedulerCount;
        this.message = message;
        this.success = success;
    }

    // Getters and Setters
    public int getSchedulerCount() {
        return schedulerCount;
    }

    public void setSchedulerCount(int schedulerCount) {
        this.schedulerCount = schedulerCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
} 