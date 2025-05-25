package com.quartz.monitor.dto.response;

import java.util.List;

import com.quartz.monitor.dto.Job;

/**
 * Recent jobs response DTO
 */
public class RecentJobsResponse {
    
    private List<Job> recentJobs;
    private int totalCount;
    
    public RecentJobsResponse() {}
    
    public RecentJobsResponse(List<Job> recentJobs, int totalCount) {
        this.recentJobs = recentJobs;
        this.totalCount = totalCount;
    }

    // Getters and Setters
    public List<Job> getRecentJobs() {
        return recentJobs;
    }

    public void setRecentJobs(List<Job> recentJobs) {
        this.recentJobs = recentJobs;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
} 