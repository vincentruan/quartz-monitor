package com.quartz.monitor.object;

import java.util.Map;
import java.util.Set;

/**
 * 作业详情类
 */
public class JobDetail {
    private Job job;
    private Map<String, String> jobDataMap;
    private Set<Trigger> triggers;
    
    public Job getJob() {
        return job;
    }
    
    public void setJob(Job job) {
        this.job = job;
    }
    
    public Map<String, String> getJobDataMap() {
        return jobDataMap;
    }
    
    public void setJobDataMap(Map<String, String> jobDataMap) {
        this.jobDataMap = jobDataMap;
    }
    
    public Set<Trigger> getTriggers() {
        return triggers;
    }
    
    public void setTriggers(Set<Trigger> triggers) {
        this.triggers = triggers;
    }
} 