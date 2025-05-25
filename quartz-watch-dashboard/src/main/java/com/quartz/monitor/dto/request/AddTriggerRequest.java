package com.quartz.monitor.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Add trigger request DTO
 */
public class AddTriggerRequest {
    
    @NotBlank(message = "Trigger name cannot be blank")
    private String name;
    
    @NotBlank(message = "Group name cannot be blank")
    private String group;
    
    private String description;
    
    @NotBlank(message = "Job ID cannot be blank")
    private String jobId;
    
    @NotNull(message = "Date flag cannot be null")
    private Integer dateFlag;
    
    private Date date;
    
    private String cron;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getDateFlag() {
        return dateFlag;
    }

    public void setDateFlag(Integer dateFlag) {
        this.dateFlag = dateFlag;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }
} 