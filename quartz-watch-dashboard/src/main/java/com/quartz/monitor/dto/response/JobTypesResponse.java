package com.quartz.monitor.dto.response;

import java.util.Map;
import java.util.Set;

import com.quartz.monitor.dto.Job;

/**
 * Job types response DTO
 */
public class JobTypesResponse {
    private Set<String> schedulers;
    private Map<String, Job> jobs;
    
    public JobTypesResponse() {}
    
    public JobTypesResponse(Set<String> schedulers, Map<String, Job> jobs) {
        this.schedulers = schedulers;
        this.jobs = jobs;
    }

    // Getters and Setters
    public Set<String> getSchedulers() {
        return schedulers;
    }

    public void setSchedulers(Set<String> schedulers) {
        this.schedulers = schedulers;
    }

    public Map<String, Job> getJobs() {
        return jobs;
    }

    public void setJobs(Map<String, Job> jobs) {
        this.jobs = jobs;
    }
} 