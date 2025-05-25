package com.quartz.monitor.dto.request;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * Request DTO for updating an existing job
 */
public class UpdateJobRequest {
    
    private String description;
    
    private Map<String, Object> jobDataMap = new HashMap<>();
    
    @NotNull(message = "Durable flag is required")
    private Boolean durable = false;
    
    @NotNull(message = "Request recovery flag is required")
    private Boolean requestRecovery = false;
    
    // Getters and setters
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Map<String, Object> getJobDataMap() {
        return jobDataMap != null ? jobDataMap : new HashMap<>();
    }
    
    public void setJobDataMap(Map<String, Object> jobDataMap) {
        this.jobDataMap = jobDataMap;
    }
    
    public boolean isDurable() {
        return durable != null ? durable : false;
    }
    
    public void setDurable(Boolean durable) {
        this.durable = durable;
    }
    
    public boolean isRequestRecovery() {
        return requestRecovery != null ? requestRecovery : false;
    }
    
    public void setRequestRecovery(Boolean requestRecovery) {
        this.requestRecovery = requestRecovery;
    }
} 