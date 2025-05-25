package com.quartz.monitor.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quartz.monitor.dto.JobInfo;
import com.quartz.monitor.dto.PageResult;
import com.quartz.monitor.dto.request.CreateJobRequest;
import com.quartz.monitor.dto.request.UpdateJobRequest;
import com.quartz.monitor.dto.response.JobDetailResponse;
import com.quartz.monitor.service.JobService;

/**
 * REST controller for managing Quartz jobs
 * Supports multiple Scheduler instances
 */
@RestController
@RequestMapping("/api/jobs")
@Validated
public class JobController {

    private static final Logger logger = LoggerFactory.getLogger(JobController.class);
    
    private final JobService jobService;
    
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }
    
    /**
     * Get paginated list of jobs from all schedulers
     * @param page page number (0-based)
     * @param size items per page
     * @param schedulerName optional scheduler name filter
     * @param groupName optional group name filter
     * @return paginated job list
     */
    @GetMapping
    public PageResult<JobInfo> getJobs(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(required = false) String schedulerName,
            @RequestParam(required = false) String groupName) {
        
        logger.debug("Getting jobs - page: {}, size: {}, scheduler: {}, group: {}", 
                     page, size, schedulerName, groupName);
        
        return jobService.getJobs(page, size, schedulerName, groupName);
    }
    
    /**
     * Get job details by job key
     * @param schedulerName scheduler name
     * @param jobName job name
     * @param jobGroup job group
     * @return job details
     */
    @GetMapping("/{schedulerName}/{jobGroup}/{jobName}")
    public JobDetailResponse getJobDetail(
            @PathVariable @NotBlank String schedulerName,
            @PathVariable @NotBlank String jobGroup,
            @PathVariable @NotBlank String jobName) {
        
        logger.debug("Getting job details - scheduler: {}, group: {}, name: {}", 
                     schedulerName, jobGroup, jobName);
        
        return jobService.getJobDetail(schedulerName, jobGroup, jobName);
    }
    
    /**
     * Create a new job
     * @param request job creation request
     * @return created job info
     */
    @PostMapping
    public JobInfo createJob(@RequestBody @Valid CreateJobRequest request) {
        logger.info("Creating job - scheduler: {}, group: {}, name: {}", 
                    request.getSchedulerName(), request.getJobGroup(), request.getJobName());
        
        return jobService.createJob(request);
    }
    
    /**
     * Update an existing job
     * @param schedulerName scheduler name
     * @param jobGroup job group
     * @param jobName job name
     * @param request update request
     * @return updated job info
     */
    @PutMapping("/{schedulerName}/{jobGroup}/{jobName}")
    public JobInfo updateJob(
            @PathVariable @NotBlank String schedulerName,
            @PathVariable @NotBlank String jobGroup,
            @PathVariable @NotBlank String jobName,
            @RequestBody @Valid UpdateJobRequest request) {
        
        logger.info("Updating job - scheduler: {}, group: {}, name: {}", 
                    schedulerName, jobGroup, jobName);
        
        return jobService.updateJob(schedulerName, jobGroup, jobName, request);
    }
    
    /**
     * Delete a job
     * @param schedulerName scheduler name
     * @param jobGroup job group
     * @param jobName job name
     */
    @DeleteMapping("/{schedulerName}/{jobGroup}/{jobName}")
    public void deleteJob(
            @PathVariable @NotBlank String schedulerName,
            @PathVariable @NotBlank String jobGroup,
            @PathVariable @NotBlank String jobName) {
        
        logger.info("Deleting job - scheduler: {}, group: {}, name: {}", 
                    schedulerName, jobGroup, jobName);
        
        jobService.deleteJob(schedulerName, jobGroup, jobName);
    }
    
    /**
     * Pause a job
     * @param schedulerName scheduler name
     * @param jobGroup job group
     * @param jobName job name
     */
    @PostMapping("/{schedulerName}/{jobGroup}/{jobName}/pause")
    public void pauseJob(
            @PathVariable @NotBlank String schedulerName,
            @PathVariable @NotBlank String jobGroup,
            @PathVariable @NotBlank String jobName) {
        
        logger.info("Pausing job - scheduler: {}, group: {}, name: {}", 
                    schedulerName, jobGroup, jobName);
        
        jobService.pauseJob(schedulerName, jobGroup, jobName);
    }
    
    /**
     * Resume a paused job
     * @param schedulerName scheduler name
     * @param jobGroup job group
     * @param jobName job name
     */
    @PostMapping("/{schedulerName}/{jobGroup}/{jobName}/resume")
    public void resumeJob(
            @PathVariable @NotBlank String schedulerName,
            @PathVariable @NotBlank String jobGroup,
            @PathVariable @NotBlank String jobName) {
        
        logger.info("Resuming job - scheduler: {}, group: {}, name: {}", 
                    schedulerName, jobGroup, jobName);
        
        jobService.resumeJob(schedulerName, jobGroup, jobName);
    }
    
    /**
     * Trigger a job immediately
     * @param schedulerName scheduler name
     * @param jobGroup job group
     * @param jobName job name
     */
    @PostMapping("/{schedulerName}/{jobGroup}/{jobName}/trigger")
    public void triggerJob(
            @PathVariable @NotBlank String schedulerName,
            @PathVariable @NotBlank String jobGroup,
            @PathVariable @NotBlank String jobName) {
        
        logger.info("Triggering job - scheduler: {}, group: {}, name: {}", 
                    schedulerName, jobGroup, jobName);
        
        jobService.triggerJob(schedulerName, jobGroup, jobName);
    }
} 