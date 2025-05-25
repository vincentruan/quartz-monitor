package com.quartz.monitor.service.impl;

import java.util.ArrayList;
import java.util.Collection;
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
import org.springframework.stereotype.Service;

import com.quartz.monitor.common.ErrorCode;
import com.quartz.monitor.dto.Job;
import com.quartz.monitor.dto.response.DashboardStatsResponse;
import com.quartz.monitor.dto.response.RecentJobsResponse;
import com.quartz.monitor.exception.BusinessException;
import com.quartz.monitor.service.DashboardService;
import com.quartz.monitor.service.SchedulerService;

/**
 * Dashboard服务实现
 */
@Service
public class DashboardServiceImpl implements DashboardService {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);
    
    private static final int MAX_RECENT_JOBS = 10;
    
    private final SchedulerService schedulerService;
    
    public DashboardServiceImpl(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
    
    @Override
    public DashboardStatsResponse getStats() {
        logger.debug("Fetching dashboard statistics for multiple schedulers");
        
        try {
            Collection<Scheduler> allSchedulers = schedulerService.getAllSchedulers();
            
            // Instance count - number of unique scheduler instances
            int instanceCount = allSchedulers.size();
            
            // Scheduler count - count of started schedulers
            int schedulerCount = 0;
            int jobCount = 0;
            int triggerCount = 0;
            int runningJobCount = 0;
            
            for (Scheduler scheduler : allSchedulers) {
                if (scheduler.isStarted()) {
                    schedulerCount++;
                    
                    // Job count - get all job keys from all groups
                    List<String> jobGroupNames = scheduler.getJobGroupNames();
                    for (String groupName : jobGroupNames) {
                        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
                        jobCount += jobKeys.size();
                    }
                    
                    // Trigger count - get all trigger keys from all groups
                    List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
                    for (String groupName : triggerGroupNames) {
                        Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(groupName));
                        triggerCount += triggerKeys.size();
                    }
                    
                    // Running job count - get currently executing jobs
                    List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
                    runningJobCount += currentlyExecutingJobs.size();
                }
            }
            
            logger.debug("Found {} instances, {} schedulers, {} jobs, {} triggers, {} running jobs", 
                         instanceCount, schedulerCount, jobCount, triggerCount, runningJobCount);
            
            DashboardStatsResponse response = new DashboardStatsResponse(
                instanceCount, schedulerCount, jobCount, triggerCount, runningJobCount
            );
            
            logger.info("Dashboard statistics retrieved successfully");
            return response;
            
        } catch (SchedulerException e) {
            logger.error("Error fetching dashboard statistics", e);
            throw new BusinessException(ErrorCode.SCHEDULER_ERROR, 
                "Failed to fetch dashboard statistics", e);
        }
    }
    
    @Override
    public RecentJobsResponse getRecentJobs() {
        logger.debug("Fetching recent jobs from multiple schedulers");
        
        try {
            List<Job> allJobs = new ArrayList<>();
            Collection<Scheduler> allSchedulers = schedulerService.getStartedSchedulers();
            
            for (Scheduler scheduler : allSchedulers) {
                String schedulerName = scheduler.getSchedulerName();
                String schedulerInstanceId = scheduler.getSchedulerInstanceId();
                
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
                            job.setSchedulerName(schedulerName);
                            job.setSchedulerInstanceId(schedulerInstanceId);
                            
                            // Get trigger state and next fire time
                            if (!triggers.isEmpty()) {
                                Trigger trigger = triggers.get(0); // Get first trigger
                                TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                                job.setState(triggerState.name());
                                
                                Date nextFireTime = trigger.getNextFireTime();
                                if (nextFireTime != null) {
                                    job.setNextFireTime(nextFireTime);
                                }
                            }
                            
                            allJobs.add(job);
                        }
                    }
                }
            }
            
            int totalCount = allJobs.size();
            
            // Sort jobs by next fire time and get the most recent ones
            List<Job> recentJobs = allJobs.stream()
                .sorted(createJobComparator())
                .limit(MAX_RECENT_JOBS)
                .collect(Collectors.toList());
            
            logger.debug("Retrieved {} recent jobs out of {} total from {} schedulers", 
                         recentJobs.size(), totalCount, allSchedulers.size());
            
            RecentJobsResponse response = new RecentJobsResponse(recentJobs, totalCount);
            logger.info("Recent jobs retrieved successfully");
            return response;
            
        } catch (SchedulerException e) {
            logger.error("Error fetching recent jobs", e);
            throw new BusinessException(ErrorCode.SCHEDULER_ERROR, 
                "Failed to fetch recent jobs", e);
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