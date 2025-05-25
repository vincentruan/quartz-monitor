package com.quartz.monitor.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Add job request DTO
 */
public class AddJobRequest {
    
    @NotBlank(message = "Job name cannot be blank")
    private String jobName;
    
    @NotBlank(message = "Group name cannot be blank")
    private String group;
    
    private String description;
    
    @NotBlank(message = "Job class cannot be blank")
    private String jobClass;
    
    @NotBlank(message = "Scheduler name cannot be blank")
    private String schedulerName;

    // Getters and Setters
    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }
} 