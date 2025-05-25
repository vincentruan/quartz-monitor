package com.quartz.monitor.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.quartz.monitor.common.ErrorCode;
import com.quartz.monitor.dto.JobInfo;
import com.quartz.monitor.dto.PageResult;
import com.quartz.monitor.dto.request.CreateJobRequest;
import com.quartz.monitor.dto.request.UpdateJobRequest;
import com.quartz.monitor.dto.response.JobDetailResponse;
import com.quartz.monitor.exception.BusinessException;
import com.quartz.monitor.exception.JobNotFoundException;
import com.quartz.monitor.service.JobService;
import com.quartz.monitor.service.SchedulerService;

/**
 * Job服务实现
 */
@Service
public class JobServiceImpl implements JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
    
    private final SchedulerService schedulerService;
    
    public JobServiceImpl(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
    
    @Override
    public PageResult<JobInfo> getJobs(int page, int size, String schedulerName, String groupName) {
        logger.debug("Fetching jobs - page: {}, size: {}, scheduler: {}, group: {}", 
                     page, size, schedulerName, groupName);
        
        List<JobInfo> allJobs = new ArrayList<>();
        
        try {
            Collection<Scheduler> targetSchedulers = schedulerService.getStartedSchedulers();
            
            // Filter by scheduler name if provided
            if (schedulerName != null && !schedulerName.isEmpty()) {
                targetSchedulers = targetSchedulers.stream()
                    .filter(s -> {
                        try {
                            return s.getSchedulerName().equals(schedulerName);
                        } catch (SchedulerException e) {
                            logger.error("Error getting scheduler name", e);
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
            }
            
            // Collect jobs from all target schedulers
            for (Scheduler scheduler : targetSchedulers) {
                String currentSchedulerName = scheduler.getSchedulerName();
                String instanceId = scheduler.getSchedulerInstanceId();
                
                // Get job groups
                List<String> jobGroups = scheduler.getJobGroupNames();
                
                // Filter by group name if provided
                if (groupName != null && !groupName.isEmpty()) {
                    jobGroups = jobGroups.stream()
                        .filter(g -> g.equals(groupName))
                        .collect(Collectors.toList());
                }
                
                for (String group : jobGroups) {
                    Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group));
                    
                    for (JobKey jobKey : jobKeys) {
                        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                        if (jobDetail != null) {
                            JobInfo jobInfo = buildJobInfo(scheduler, jobDetail);
                            allJobs.add(jobInfo);
                        }
                    }
                }
            }
            
            // Create paginated result
            int totalElements = allJobs.size();
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, totalElements);
            
            List<JobInfo> pageContent = fromIndex < totalElements ? 
                allJobs.subList(fromIndex, toIndex) : new ArrayList<>();
            
            logger.info("Retrieved {} jobs out of {} total (page {} of {})", 
                        pageContent.size(), totalElements, page + 1, 
                        (totalElements + size - 1) / size);
            
            return new PageResult<>(pageContent, page, size, totalElements);
            
        } catch (SchedulerException e) {
            logger.error("Error fetching jobs", e);
            throw new BusinessException(ErrorCode.JOB_OPERATION_FAILED, "Failed to fetch jobs", e);
        }
    }
    
    @Override
    public JobDetailResponse getJobDetail(String schedulerName, String jobGroup, String jobName) {
        logger.debug("Fetching job details - scheduler: {}, group: {}, name: {}", 
                     schedulerName, jobGroup, jobName);
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(schedulerName);
            JobKey jobKey = new JobKey(jobName, jobGroup);
            
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail == null) {
                throw new JobNotFoundException("Job not found: " + jobKey);
            }
            
            JobInfo jobInfo = buildJobInfo(scheduler, jobDetail);
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            
            logger.info("Retrieved job details for: {}", jobKey);
            return new JobDetailResponse(jobInfo, triggers);
            
        } catch (SchedulerException e) {
            logger.error("Error fetching job details", e);
            throw new BusinessException(ErrorCode.JOB_OPERATION_FAILED, "Failed to fetch job details", e);
        }
    }
    
    @Override
    public JobInfo createJob(CreateJobRequest request) {
        logger.info("Creating new job - scheduler: {}, group: {}, name: {}", 
                    request.getSchedulerName(), request.getJobGroup(), request.getJobName());
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(request.getSchedulerName());
            
            // Load job class
            Class<?> jobClass = Class.forName(request.getJobClassName());
            if (!org.quartz.Job.class.isAssignableFrom(jobClass)) {
                throw new BusinessException(ErrorCode.JOB_CLASS_INVALID, 
                    "Invalid job class: " + request.getJobClassName());
            }
            
            // Build job detail
            JobDetail jobDetail = JobBuilder.newJob((Class<? extends org.quartz.Job>) jobClass)
                .withIdentity(request.getJobName(), request.getJobGroup())
                .withDescription(request.getDescription())
                .storeDurably(request.isDurable())
                .requestRecovery(request.isRequestRecovery())
                .usingJobData(new JobDataMap(request.getJobDataMap()))
                .build();
            
            // Add job to scheduler
            scheduler.addJob(jobDetail, request.isReplace());
            
            JobInfo jobInfo = buildJobInfo(scheduler, jobDetail);
            logger.info("Successfully created job: {}", jobDetail.getKey());
            
            return jobInfo;
            
        } catch (ClassNotFoundException e) {
            logger.error("Job class not found: {}", request.getJobClassName(), e);
            throw new BusinessException(ErrorCode.JOB_CLASS_NOT_FOUND, 
                "Job class not found: " + request.getJobClassName());
        } catch (SchedulerException e) {
            logger.error("Error creating job", e);
            throw new BusinessException(ErrorCode.JOB_CREATE_FAILED, "Failed to create job", e);
        }
    }
    
    @Override
    public JobInfo updateJob(String schedulerName, String jobGroup, String jobName, 
                           UpdateJobRequest request) {
        logger.info("Updating job - scheduler: {}, group: {}, name: {}", 
                    schedulerName, jobGroup, jobName);
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(schedulerName);
            JobKey jobKey = new JobKey(jobName, jobGroup);
            
            JobDetail existingJob = scheduler.getJobDetail(jobKey);
            if (existingJob == null) {
                throw new JobNotFoundException("Job not found: " + jobKey);
            }
            
            // Build updated job detail
            JobDetail updatedJob = existingJob.getJobBuilder()
                .withDescription(request.getDescription())
                .usingJobData(new JobDataMap(request.getJobDataMap()))
                .storeDurably(request.isDurable())
                .requestRecovery(request.isRequestRecovery())
                .build();
            
            // Replace job
            scheduler.addJob(updatedJob, true);
            
            JobInfo jobInfo = buildJobInfo(scheduler, updatedJob);
            logger.info("Successfully updated job: {}", jobKey);
            
            return jobInfo;
            
        } catch (SchedulerException e) {
            logger.error("Error updating job", e);
            throw new BusinessException(ErrorCode.JOB_UPDATE_FAILED, "Failed to update job", e);
        }
    }
    
    @Override
    public void deleteJob(String schedulerName, String jobGroup, String jobName) {
        logger.info("Deleting job - scheduler: {}, group: {}, name: {}", 
                    schedulerName, jobGroup, jobName);
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(schedulerName);
            JobKey jobKey = new JobKey(jobName, jobGroup);
            
            if (!scheduler.checkExists(jobKey)) {
                throw new JobNotFoundException("Job not found: " + jobKey);
            }
            
            boolean deleted = scheduler.deleteJob(jobKey);
            if (!deleted) {
                throw new BusinessException(ErrorCode.JOB_DELETE_FAILED, 
                    "Failed to delete job: " + jobKey);
            }
            
            logger.info("Successfully deleted job: {}", jobKey);
            
        } catch (SchedulerException e) {
            logger.error("Error deleting job", e);
            throw new BusinessException(ErrorCode.JOB_DELETE_FAILED, "Failed to delete job", e);
        }
    }
    
    @Override
    public void pauseJob(String schedulerName, String jobGroup, String jobName) {
        logger.info("Pausing job - scheduler: {}, group: {}, name: {}", 
                    schedulerName, jobGroup, jobName);
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(schedulerName);
            JobKey jobKey = new JobKey(jobName, jobGroup);
            
            if (!scheduler.checkExists(jobKey)) {
                throw new JobNotFoundException("Job not found: " + jobKey);
            }
            
            scheduler.pauseJob(jobKey);
            logger.info("Successfully paused job: {}", jobKey);
            
        } catch (SchedulerException e) {
            logger.error("Error pausing job", e);
            throw new BusinessException(ErrorCode.JOB_OPERATION_FAILED, "Failed to pause job", e);
        }
    }
    
    @Override
    public void resumeJob(String schedulerName, String jobGroup, String jobName) {
        logger.info("Resuming job - scheduler: {}, group: {}, name: {}", 
                    schedulerName, jobGroup, jobName);
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(schedulerName);
            JobKey jobKey = new JobKey(jobName, jobGroup);
            
            if (!scheduler.checkExists(jobKey)) {
                throw new JobNotFoundException("Job not found: " + jobKey);
            }
            
            scheduler.resumeJob(jobKey);
            logger.info("Successfully resumed job: {}", jobKey);
            
        } catch (SchedulerException e) {
            logger.error("Error resuming job", e);
            throw new BusinessException(ErrorCode.JOB_OPERATION_FAILED, "Failed to resume job", e);
        }
    }
    
    @Override
    public void triggerJob(String schedulerName, String jobGroup, String jobName) {
        logger.info("Triggering job - scheduler: {}, group: {}, name: {}", 
                    schedulerName, jobGroup, jobName);
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(schedulerName);
            JobKey jobKey = new JobKey(jobName, jobGroup);
            
            if (!scheduler.checkExists(jobKey)) {
                throw new JobNotFoundException("Job not found: " + jobKey);
            }
            
            scheduler.triggerJob(jobKey);
            logger.info("Successfully triggered job: {}", jobKey);
            
        } catch (SchedulerException e) {
            logger.error("Error triggering job", e);
            throw new BusinessException(ErrorCode.JOB_OPERATION_FAILED, "Failed to trigger job", e);
        }
    }
    
    /**
     * Build JobInfo from JobDetail
     * @param scheduler scheduler instance
     * @param jobDetail job detail
     * @return job info
     * @throws SchedulerException if error occurs
     */
    private JobInfo buildJobInfo(Scheduler scheduler, JobDetail jobDetail) throws SchedulerException {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setSchedulerName(scheduler.getSchedulerName());
        jobInfo.setSchedulerInstanceId(scheduler.getSchedulerInstanceId());
        jobInfo.setJobName(jobDetail.getKey().getName());
        jobInfo.setJobGroup(jobDetail.getKey().getGroup());
        jobInfo.setJobClassName(jobDetail.getJobClass().getName());
        jobInfo.setDescription(jobDetail.getDescription());
        jobInfo.setDurable(jobDetail.isDurable());
        jobInfo.setPersistJobDataAfterExecution(jobDetail.isPersistJobDataAfterExecution());
        jobInfo.setConcurrentExecutionDisallowed(jobDetail.isConcurrentExectionDisallowed());
        jobInfo.setRequestRecovery(jobDetail.requestsRecovery());
        jobInfo.setJobDataMap(jobDetail.getJobDataMap().getWrappedMap());
        
        // Get trigger state
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
        if (!triggers.isEmpty()) {
            Trigger trigger = triggers.get(0);
            jobInfo.setTriggerState(scheduler.getTriggerState(trigger.getKey()).name());
            
            Date nextFireTime = trigger.getNextFireTime();
            if (nextFireTime != null) {
                jobInfo.setNextFireTime(nextFireTime);
            }
            
            Date previousFireTime = trigger.getPreviousFireTime();
            if (previousFireTime != null) {
                jobInfo.setPreviousFireTime(previousFireTime);
            }
        }
        
        // Check if currently executing
        List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
        boolean isExecuting = executingJobs.stream()
            .anyMatch(ctx -> ctx.getJobDetail().getKey().equals(jobDetail.getKey()));
        jobInfo.setExecuting(isExecuting);
        
        return jobInfo;
    }
} 