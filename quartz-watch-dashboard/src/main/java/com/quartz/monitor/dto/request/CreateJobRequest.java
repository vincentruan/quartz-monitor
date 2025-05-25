package com.quartz.monitor.dto.request;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Request DTO for creating a new job
 */
public class CreateJobRequest {
    
    @NotBlank(message = "Scheduler name is required")
    private String schedulerName;
    
    @NotBlank(message = "Job name is required")
    private String jobName;
    
    @NotBlank(message = "Job group is required")
    private String jobGroup;
    
    @NotBlank(message = "Job class name is required")
    private String jobClassName;
    
    private String description;
    
    @NotNull(message = "Durable flag is required")
    private Boolean durable = false;
    
    @NotNull(message = "Request recovery flag is required")
    private Boolean requestRecovery = false;
    
    private Map<String, Object> jobDataMap = new HashMap<>();
    
    @NotNull(message = "Replace flag is required")
    private Boolean replace = false;
    
    // Getters and setters
    public String getSchedulerName() {
        return schedulerName;
    }
    
    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }
    
    public String getJobName() {
        return jobName;
    }
    
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    
    public String getJobGroup() {
        return jobGroup;
    }
    
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }
    
    public String getJobClassName() {
        return jobClassName;
    }
    
    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public Map<String, Object> getJobDataMap() {
        return jobDataMap != null ? jobDataMap : new HashMap<>();
    }
    
    public void setJobDataMap(Map<String, Object> jobDataMap) {
        this.jobDataMap = jobDataMap;
    }
    
    public boolean isReplace() {
        return replace != null ? replace : false;
    }
    
    public void setReplace(Boolean replace) {
        this.replace = replace;
    }
} 