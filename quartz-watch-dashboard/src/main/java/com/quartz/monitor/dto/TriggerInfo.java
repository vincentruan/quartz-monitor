package com.quartz.monitor.dto;

import java.util.Date;

/**
 * Trigger information DTO
 */
public class TriggerInfo {
    
    private String schedulerName;
    private String schedulerInstanceId;
    private String triggerName;
    private String triggerGroup;
    private String jobName;
    private String jobGroup;
    private String description;
    private String calendarName;
    private int priority;
    private boolean mayFireAgain;
    private Date startTime;
    private Date endTime;
    private Date nextFireTime;
    private Date previousFireTime;
    private Date finalFireTime;
    private int misfireInstruction;
    private String triggerState;
    private String triggerType;
    
    // Cron trigger specific
    private String cronExpression;
    private String timeZone;
    
    // Simple trigger specific
    private int repeatCount;
    private long repeatInterval;
    private int timesTriggered;
    
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
    
    public String getCalendarName() {
        return calendarName;
    }
    
    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public boolean isMayFireAgain() {
        return mayFireAgain;
    }
    
    public void setMayFireAgain(boolean mayFireAgain) {
        this.mayFireAgain = mayFireAgain;
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
    
    public Date getFinalFireTime() {
        return finalFireTime;
    }
    
    public void setFinalFireTime(Date finalFireTime) {
        this.finalFireTime = finalFireTime;
    }
    
    public int getMisfireInstruction() {
        return misfireInstruction;
    }
    
    public void setMisfireInstruction(int misfireInstruction) {
        this.misfireInstruction = misfireInstruction;
    }
    
    public String getTriggerState() {
        return triggerState;
    }
    
    public void setTriggerState(String triggerState) {
        this.triggerState = triggerState;
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
    
    public String getTimeZone() {
        return timeZone;
    }
    
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
    
    public int getRepeatCount() {
        return repeatCount;
    }
    
    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }
    
    public long getRepeatInterval() {
        return repeatInterval;
    }
    
    public void setRepeatInterval(long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }
    
    public int getTimesTriggered() {
        return timesTriggered;
    }
    
    public void setTimesTriggered(int timesTriggered) {
        this.timesTriggered = timesTriggered;
    }
} 