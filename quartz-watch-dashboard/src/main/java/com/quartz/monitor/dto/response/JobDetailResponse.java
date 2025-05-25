package com.quartz.monitor.dto.response;

import java.util.List;

import org.quartz.Trigger;

import com.quartz.monitor.dto.JobInfo;

/**
 * Response DTO for job details
 */
public class JobDetailResponse {
    
    private JobInfo jobInfo;
    private List<? extends Trigger> triggers;
    
    public JobDetailResponse() {
    }
    
    public JobDetailResponse(JobInfo jobInfo, List<? extends Trigger> triggers) {
        this.jobInfo = jobInfo;
        this.triggers = triggers;
    }
    
    // Getters and setters
    public JobInfo getJobInfo() {
        return jobInfo;
    }
    
    public void setJobInfo(JobInfo jobInfo) {
        this.jobInfo = jobInfo;
    }
    
    public List<? extends Trigger> getTriggers() {
        return triggers;
    }
    
    public void setTriggers(List<? extends Trigger> triggers) {
        this.triggers = triggers;
    }
} 