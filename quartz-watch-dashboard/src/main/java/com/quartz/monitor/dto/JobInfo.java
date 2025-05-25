package com.quartz.monitor.dto;

import java.util.Date;
import java.util.Map;

/**
 * Job information DTO
 */
public class JobInfo {
    
    private String schedulerName;
    private String schedulerInstanceId;
    private String jobName;
    private String jobGroup;
    private String jobClassName;
    private String description;
    private boolean durable;
    private boolean persistJobDataAfterExecution;
    private boolean concurrentExecutionDisallowed;
    private boolean requestRecovery;
    private Map<String, Object> jobDataMap;
    private String triggerState;
    private Date nextFireTime;
    private Date previousFireTime;
    private boolean executing;
    
    // Getters and setters
    public String getSchedulerName() {
        return schedulerName;
    }
    
    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }
    
    public String getSchedulerInstanceId() {
        return schedulerInstanceId;
    }
    
    public void setSchedulerInstanceId(String schedulerInstanceId) {
        this.schedulerInstanceId = schedulerInstanceId;
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
        return durable;
    }
    
    public void setDurable(boolean durable) {
        this.durable = durable;
    }
    
    public boolean isPersistJobDataAfterExecution() {
        return persistJobDataAfterExecution;
    }
    
    public void setPersistJobDataAfterExecution(boolean persistJobDataAfterExecution) {
        this.persistJobDataAfterExecution = persistJobDataAfterExecution;
    }
    
    public boolean isConcurrentExecutionDisallowed() {
        return concurrentExecutionDisallowed;
    }
    
    public void setConcurrentExecutionDisallowed(boolean concurrentExecutionDisallowed) {
        this.concurrentExecutionDisallowed = concurrentExecutionDisallowed;
    }
    
    public boolean isRequestRecovery() {
        return requestRecovery;
    }
    
    public void setRequestRecovery(boolean requestRecovery) {
        this.requestRecovery = requestRecovery;
    }
    
    public Map<String, Object> getJobDataMap() {
        return jobDataMap;
    }
    
    public void setJobDataMap(Map<String, Object> jobDataMap) {
        this.jobDataMap = jobDataMap;
    }
    
    public String getTriggerState() {
        return triggerState;
    }
    
    public void setTriggerState(String triggerState) {
        this.triggerState = triggerState;
    }
    
    public Date getNextFireTime() {
        return nextFireTime;
    }
    
    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }
    
    public Date getPreviousFireTime() {
        return previousFireTime;
    }
    
    public void setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }
    
    public boolean isExecuting() {
        return executing;
    }
    
    public void setExecuting(boolean executing) {
        this.executing = executing;
    }
} 