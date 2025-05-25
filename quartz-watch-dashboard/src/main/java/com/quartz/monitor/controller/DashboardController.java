package com.quartz.monitor.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quartz.monitor.dto.Job;
import com.quartz.monitor.dto.response.DashboardStatsResponse;
import com.quartz.monitor.dto.response.RecentJobsResponse;

/**
 * REST controller for dashboard operations
 * Provides system statistics and recent job information
 */
@RestController
@RequestMapping("/api/dashboard")
@Validated
public class DashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    
    private static final int MAX_RECENT_JOBS = 10;
    
    @Autowired
    private Scheduler scheduler;
    
    /**
     * Get system statistics including instance, scheduler, job, and trigger counts
     * @return dashboard statistics response
     */
    @GetMapping("/stats")
    public DashboardStatsResponse getStats() {
        logger.debug("Fetching dashboard statistics using Spring Boot Quartz");
        
        try {
            // Instance count - in Spring Boot typically we have one instance
            int instanceCount = scheduler.getSchedulerName() != null ? 1 : 0;
            
            // Scheduler count - in Spring Boot typically we have one scheduler
            int schedulerCount = scheduler.isStarted() ? 1 : 0;
            
            // Job count - get all job keys from all groups
            int jobCount = 0;
            List<String> jobGroupNames = scheduler.getJobGroupNames();
            for (String groupName : jobGroupNames) {
                Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
                jobCount += jobKeys.size();
            }
            logger.debug("Found {} jobs", jobCount);
            
            // Trigger count - get all trigger keys from all groups
            int triggerCount = 0;
            List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
            for (String groupName : triggerGroupNames) {
                Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(groupName));
                triggerCount += triggerKeys.size();
            }
            logger.debug("Found {} triggers", triggerCount);
            
            // Running job count - get currently executing jobs
            List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
            int runningJobCount = currentlyExecutingJobs.size();
            logger.debug("Found {} running jobs", runningJobCount);
            
            DashboardStatsResponse response = new DashboardStatsResponse(
                instanceCount, schedulerCount, jobCount, triggerCount, runningJobCount
            );
            
            logger.info("Dashboard statistics retrieved successfully");
            return response;
            
        } catch (SchedulerException e) {
            logger.error("Error fetching dashboard statistics", e);
            throw new RuntimeException("Failed to fetch dashboard statistics", e);
        }
    }
    
    /**
     * Get recently executed or scheduled jobs
     * @return recent jobs response
     */
    @GetMapping("/recent-jobs")
    public RecentJobsResponse getRecentJobs() {
        logger.debug("Fetching recent jobs using Spring Boot Quartz");
        
        try {
            List<Job> allJobs = new ArrayList<>();
            
            // Get all job groups
            List<String> jobGroupNames = scheduler.getJobGroupNames();
            
            for (String groupName : jobGroupNames) {
                Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
                
                for (JobKey jobKey : jobKeys) {
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    if (jobDetail != null) {
                        // Get triggers for this job
                        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                        
                        // Create Job DTO
                        Job job = new Job();
                        job.setJobName(jobKey.getName());
                        job.setGroup(jobKey.getGroup());
                        job.setJobClass(jobDetail.getJobClass().getName());
                        job.setDescription(jobDetail.getDescription());
                        
                        // Get trigger state and next fire time
                        if (!triggers.isEmpty()) {
                            Trigger trigger = triggers.get(0); // Get first trigger
                            TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                            job.setState(triggerState.name());
                            
                            Date nextFireTime = trigger.getNextFireTime();
                            if (nextFireTime != null) {
                                job.setNextFireTime(nextFireTime);
                            }
                            
                            // Note: Job DTO doesn't have previousFireTime field
                            // If needed, add this field to the Job class
                        }
                        
                        allJobs.add(job);
                    }
                }
            }
            
            int totalCount = allJobs.size();
            
            // Sort jobs by next fire time and get the most recent ones
            List<Job> recentJobs = allJobs.stream()
                .sorted(createJobComparator())
                .limit(MAX_RECENT_JOBS)
                .collect(Collectors.toList());
            
            logger.debug("Retrieved {} recent jobs out of {} total", recentJobs.size(), totalCount);
            
            RecentJobsResponse response = new RecentJobsResponse(recentJobs, totalCount);
            logger.info("Recent jobs retrieved successfully");
            return response;
            
        } catch (SchedulerException e) {
            logger.error("Error fetching recent jobs", e);
            throw new RuntimeException("Failed to fetch recent jobs", e);
        }
    }
    
    /**
     * Create comparator for sorting jobs by next fire time
     * @return job comparator
     */
    private Comparator<Job> createJobComparator() {
        return (j1, j2) -> {
            if (j1.getNextFireTime() == null && j2.getNextFireTime() == null) {
                return 0;
            } else if (j1.getNextFireTime() == null) {
                return 1;
            } else if (j2.getNextFireTime() == null) {
                return -1;
            } else {
                return j1.getNextFireTime().compareTo(j2.getNextFireTime());
            }
        };
    }
} 