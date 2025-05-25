package com.quartz.monitor.dto.request;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.quartz.Trigger;

/**
 * Request DTO for creating a new trigger
 */
public class CreateTriggerRequest {
    
    @NotBlank(message = "Scheduler name is required")
    private String schedulerName;
    
    @NotBlank(message = "Trigger name is required")
    private String triggerName;
    
    @NotBlank(message = "Trigger group is required")
    private String triggerGroup;
    
    @NotBlank(message = "Job name is required")
    private String jobName;
    
    @NotBlank(message = "Job group is required")
    private String jobGroup;
    
    private String description;
    
    @NotNull(message = "Trigger type is required")
    private String triggerType; // "CRON" or "SIMPLE"
    
    // Cron trigger specific
    private String cronExpression;
    
    // Simple trigger specific
    private Integer repeatCount;
    private Long repeatInterval;
    
    // Common fields
    private Date startTime;
    private Date endTime;
    private Integer priority = Trigger.DEFAULT_PRIORITY;
    private String calendarName;
    
    // Getters and setters
    public String getSchedulerName() {
        return schedulerName;
    }
    
    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }
    
    public String getTriggerName() {
        return triggerName;
    }
    
    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }
    
    public String getTriggerGroup() {
        return triggerGroup;
    }
    
    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getTriggerType() {
        return triggerType;
    }
    
    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }
    
    public String getCronExpression() {
        return cronExpression;
    }
    
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
    
    public Integer getRepeatCount() {
        return repeatCount;
    }
    
    public void setRepeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
    }
    
    public Long getRepeatInterval() {
        return repeatInterval;
    }
    
    public void setRepeatInterval(Long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }
    
    public Date getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    public Date getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    
    public Integer getPriority() {
        return priority != null ? priority : Trigger.DEFAULT_PRIORITY;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public String getCalendarName() {
        return calendarName;
    }
    
    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }
} 