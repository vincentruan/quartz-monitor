package com.quartz.monitor.dto.response;

/**
 * Dashboard statistics response DTO
 */
public class DashboardStatsResponse {
    
    private int instanceCount;
    private int schedulerCount;
    private int jobCount;
    private int triggerCount;
    private int runningJobCount;
    
    public DashboardStatsResponse() {}
    
    public DashboardStatsResponse(int instanceCount, int schedulerCount, int jobCount, 
                                 int triggerCount, int runningJobCount) {
        this.instanceCount = instanceCount;
        this.schedulerCount = schedulerCount;
        this.jobCount = jobCount;
        this.triggerCount = triggerCount;
        this.runningJobCount = runningJobCount;
    }

    // Getters and Setters
    public int getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
    }

    public int getSchedulerCount() {
        return schedulerCount;
    }

    public void setSchedulerCount(int schedulerCount) {
        this.schedulerCount = schedulerCount;
    }

    public int getJobCount() {
        return jobCount;
    }

    public void setJobCount(int jobCount) {
        this.jobCount = jobCount;
    }

    public int getTriggerCount() {
        return triggerCount;
    }

    public void setTriggerCount(int triggerCount) {
        this.triggerCount = triggerCount;
    }

    public int getRunningJobCount() {
        return runningJobCount;
    }

    public void setRunningJobCount(int runningJobCount) {
        this.runningJobCount = runningJobCount;
    }
} 