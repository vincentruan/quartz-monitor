package com.quartz.monitor.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.quartz.monitor.common.ErrorCode;
import com.quartz.monitor.dto.PageResult;
import com.quartz.monitor.dto.TriggerInfo;
import com.quartz.monitor.dto.request.CreateTriggerRequest;
import com.quartz.monitor.dto.request.UpdateTriggerRequest;
import com.quartz.monitor.dto.response.TriggerDetailResponse;
import com.quartz.monitor.exception.BusinessException;
import com.quartz.monitor.exception.JobNotFoundException;
import com.quartz.monitor.exception.TriggerNotFoundException;
import com.quartz.monitor.service.SchedulerService;
import com.quartz.monitor.service.TriggerService;

/**
 * Trigger服务实现
 */
@Service
public class TriggerServiceImpl implements TriggerService {
    
    private static final Logger logger = LoggerFactory.getLogger(TriggerServiceImpl.class);
    
    private final SchedulerService schedulerService;
    
    public TriggerServiceImpl(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
    
    @Override
    public PageResult<TriggerInfo> getTriggersForJob(String schedulerName, String jobGroup, 
                                                   String jobName, int page, int size) {
        logger.debug("Fetching triggers for job - scheduler: {}, group: {}, name: {}", 
                     schedulerName, jobGroup, jobName);
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(schedulerName);
            JobKey jobKey = new JobKey(jobName, jobGroup);
            
            if (!scheduler.checkExists(jobKey)) {
                throw new JobNotFoundException("Job not found: " + jobKey);
            }
            
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            List<TriggerInfo> triggerInfos = new ArrayList<>();
            
            for (Trigger trigger : triggers) {
                TriggerInfo triggerInfo = buildTriggerInfo(scheduler, trigger, jobKey);
                triggerInfos.add(triggerInfo);
            }
            
            // Create paginated result
            int totalElements = triggerInfos.size();
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, totalElements);
            
            List<TriggerInfo> pageContent = fromIndex < totalElements ? 
                triggerInfos.subList(fromIndex, toIndex) : new ArrayList<>();
            
            logger.info("Retrieved {} triggers for job: {} (page {} of {})", 
                        pageContent.size(), jobKey, page + 1, 
                        (totalElements + size - 1) / size);
            
            return new PageResult<>(pageContent, page, size, totalElements);
            
        } catch (SchedulerException e) {
            logger.error("Error fetching triggers", e);
            throw new BusinessException(ErrorCode.TRIGGER_OPERATION_FAILED, 
                "Failed to fetch triggers", e);
        }
    }
    
    @Override
    public TriggerDetailResponse getTriggerDetail(String schedulerName, String triggerGroup, 
                                                String triggerName) {
        logger.debug("Fetching trigger details - scheduler: {}, group: {}, name: {}", 
                     schedulerName, triggerGroup, triggerName);
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(schedulerName);
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
            
            Trigger trigger = scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                throw new TriggerNotFoundException("Trigger not found: " + triggerKey);
            }
            
            TriggerInfo triggerInfo = buildTriggerInfo(scheduler, trigger, trigger.getJobKey());
            
            logger.info("Retrieved trigger details for: {}", triggerKey);
            return new TriggerDetailResponse(triggerInfo);
            
        } catch (SchedulerException e) {
            logger.error("Error fetching trigger details", e);
            throw new BusinessException(ErrorCode.TRIGGER_OPERATION_FAILED, 
                "Failed to fetch trigger details", e);
        }
    }
    
    @Override
    public TriggerInfo createTrigger(CreateTriggerRequest request) {
        logger.info("Creating new trigger - scheduler: {}, group: {}, name: {}", 
                    request.getSchedulerName(), request.getTriggerGroup(), request.getTriggerName());
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(request.getSchedulerName());
            JobKey jobKey = new JobKey(request.getJobName(), request.getJobGroup());
            
            if (!scheduler.checkExists(jobKey)) {
                throw new JobNotFoundException("Job not found: " + jobKey);
            }
            
            Trigger trigger = buildTrigger(request);
            scheduler.scheduleJob(trigger);
            
            TriggerInfo triggerInfo = buildTriggerInfo(scheduler, trigger, jobKey);
            logger.info("Successfully created trigger: {}", trigger.getKey());
            
            return triggerInfo;
            
        } catch (ParseException e) {
            logger.error("Invalid cron expression: {}", request.getCronExpression(), e);
            throw new BusinessException(ErrorCode.TRIGGER_CRON_INVALID, 
                "Invalid cron expression", e);
        } catch (SchedulerException e) {
            logger.error("Error creating trigger", e);
            throw new BusinessException(ErrorCode.TRIGGER_CREATE_FAILED, 
                "Failed to create trigger", e);
        }
    }
    
    @Override
    public TriggerInfo updateTrigger(String schedulerName, String triggerGroup, 
                                   String triggerName, UpdateTriggerRequest request) {
        logger.info("Updating trigger - scheduler: {}, group: {}, name: {}", 
                    schedulerName, triggerGroup, triggerName);
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(schedulerName);
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
            
            Trigger existingTrigger = scheduler.getTrigger(triggerKey);
            if (existingTrigger == null) {
                throw new TriggerNotFoundException("Trigger not found: " + triggerKey);
            }
            
            Trigger updatedTrigger = buildUpdatedTrigger(existingTrigger, request);
            scheduler.rescheduleJob(triggerKey, updatedTrigger);
            
            TriggerInfo triggerInfo = buildTriggerInfo(scheduler, updatedTrigger, 
                                                      updatedTrigger.getJobKey());
            logger.info("Successfully updated trigger: {}", triggerKey);
            
            return triggerInfo;
            
        } catch (ParseException e) {
            logger.error("Invalid cron expression: {}", request.getCronExpression(), e);
            throw new BusinessException(ErrorCode.TRIGGER_CRON_INVALID, 
                "Invalid cron expression", e);
        } catch (SchedulerException e) {
            logger.error("Error updating trigger", e);
            throw new BusinessException(ErrorCode.TRIGGER_UPDATE_FAILED, 
                "Failed to update trigger", e);
        }
    }
    
    @Override
    public void deleteTrigger(String schedulerName, String triggerGroup, String triggerName) {
        logger.info("Deleting trigger - scheduler: {}, group: {}, name: {}", 
                    schedulerName, triggerGroup, triggerName);
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(schedulerName);
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
            
            if (!scheduler.checkExists(triggerKey)) {
                throw new TriggerNotFoundException("Trigger not found: " + triggerKey);
            }
            
            boolean deleted = scheduler.unscheduleJob(triggerKey);
            if (!deleted) {
                throw new BusinessException(ErrorCode.TRIGGER_DELETE_FAILED, 
                    "Failed to delete trigger: " + triggerKey);
            }
            
            logger.info("Successfully deleted trigger: {}", triggerKey);
            
        } catch (SchedulerException e) {
            logger.error("Error deleting trigger", e);
            throw new BusinessException(ErrorCode.TRIGGER_DELETE_FAILED, 
                "Failed to delete trigger", e);
        }
    }
    
    @Override
    public void pauseTrigger(String schedulerName, String triggerGroup, String triggerName) {
        logger.info("Pausing trigger - scheduler: {}, group: {}, name: {}", 
                    schedulerName, triggerGroup, triggerName);
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(schedulerName);
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
            
            if (!scheduler.checkExists(triggerKey)) {
                throw new TriggerNotFoundException("Trigger not found: " + triggerKey);
            }
            
            scheduler.pauseTrigger(triggerKey);
            logger.info("Successfully paused trigger: {}", triggerKey);
            
        } catch (SchedulerException e) {
            logger.error("Error pausing trigger", e);
            throw new BusinessException(ErrorCode.TRIGGER_OPERATION_FAILED, 
                "Failed to pause trigger", e);
        }
    }
    
    @Override
    public void resumeTrigger(String schedulerName, String triggerGroup, String triggerName) {
        logger.info("Resuming trigger - scheduler: {}, group: {}, name: {}", 
                    schedulerName, triggerGroup, triggerName);
        
        try {
            Scheduler scheduler = schedulerService.getSchedulerByName(schedulerName);
            TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
            
            if (!scheduler.checkExists(triggerKey)) {
                throw new TriggerNotFoundException("Trigger not found: " + triggerKey);
            }
            
            scheduler.resumeTrigger(triggerKey);
            logger.info("Successfully resumed trigger: {}", triggerKey);
            
        } catch (SchedulerException e) {
            logger.error("Error resuming trigger", e);
            throw new BusinessException(ErrorCode.TRIGGER_OPERATION_FAILED, 
                "Failed to resume trigger", e);
        }
    }
    
    /**
     * Build TriggerInfo from Trigger
     * @param scheduler scheduler instance
     * @param trigger trigger instance
     * @param jobKey job key
     * @return trigger info
     * @throws SchedulerException if error occurs
     */
    private TriggerInfo buildTriggerInfo(Scheduler scheduler, Trigger trigger, JobKey jobKey) 
            throws SchedulerException {
        TriggerInfo triggerInfo = new TriggerInfo();
        triggerInfo.setSchedulerName(scheduler.getSchedulerName());
        triggerInfo.setSchedulerInstanceId(scheduler.getSchedulerInstanceId());
        triggerInfo.setTriggerName(trigger.getKey().getName());
        triggerInfo.setTriggerGroup(trigger.getKey().getGroup());
        triggerInfo.setJobName(jobKey.getName());
        triggerInfo.setJobGroup(jobKey.getGroup());
        triggerInfo.setDescription(trigger.getDescription());
        triggerInfo.setCalendarName(trigger.getCalendarName());
        triggerInfo.setPriority(trigger.getPriority());
        triggerInfo.setMayFireAgain(trigger.mayFireAgain());
        triggerInfo.setStartTime(trigger.getStartTime());
        triggerInfo.setEndTime(trigger.getEndTime());
        triggerInfo.setNextFireTime(trigger.getNextFireTime());
        triggerInfo.setPreviousFireTime(trigger.getPreviousFireTime());
        triggerInfo.setFinalFireTime(trigger.getFinalFireTime());
        triggerInfo.setMisfireInstruction(trigger.getMisfireInstruction());
        triggerInfo.setTriggerState(scheduler.getTriggerState(trigger.getKey()).name());
        
        if (trigger instanceof CronTrigger) {
            CronTrigger cronTrigger = (CronTrigger) trigger;
            triggerInfo.setTriggerType("CRON");
            triggerInfo.setCronExpression(cronTrigger.getCronExpression());
            triggerInfo.setTimeZone(cronTrigger.getTimeZone().getID());
        } else if (trigger instanceof SimpleTrigger) {
            SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
            triggerInfo.setTriggerType("SIMPLE");
            triggerInfo.setRepeatCount(simpleTrigger.getRepeatCount());
            triggerInfo.setRepeatInterval(simpleTrigger.getRepeatInterval());
            triggerInfo.setTimesTriggered(simpleTrigger.getTimesTriggered());
        } else {
            triggerInfo.setTriggerType(trigger.getClass().getSimpleName());
        }
        
        return triggerInfo;
    }
    
    /**
     * Build trigger from creation request
     * @param request creation request
     * @return trigger instance
     * @throws ParseException if cron expression is invalid
     */
    private Trigger buildTrigger(CreateTriggerRequest request) throws ParseException {
        JobKey jobKey = new JobKey(request.getJobName(), request.getJobGroup());
        
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
            .withIdentity(request.getTriggerName(), request.getTriggerGroup())
            .forJob(jobKey)
            .withDescription(request.getDescription())
            .withPriority(request.getPriority());
        
        if (request.getStartTime() != null) {
            triggerBuilder.startAt(request.getStartTime());
        } else {
            triggerBuilder.startNow();
        }
        
        if (request.getEndTime() != null) {
            triggerBuilder.endAt(request.getEndTime());
        }
        
        if (request.getCalendarName() != null) {
            triggerBuilder.modifiedByCalendar(request.getCalendarName());
        }
        
        if ("CRON".equals(request.getTriggerType())) {
            triggerBuilder.withSchedule(
                CronScheduleBuilder.cronSchedule(request.getCronExpression())
                    .withMisfireHandlingInstructionDoNothing()
            );
        } else if ("SIMPLE".equals(request.getTriggerType())) {
            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
            
            if (request.getRepeatInterval() != null && request.getRepeatInterval() > 0) {
                scheduleBuilder.withIntervalInMilliseconds(request.getRepeatInterval());
                
                if (request.getRepeatCount() != null) {
                    if (request.getRepeatCount() == -1) {
                        scheduleBuilder.repeatForever();
                    } else {
                        scheduleBuilder.withRepeatCount(request.getRepeatCount());
                    }
                }
            }
            
            triggerBuilder.withSchedule(scheduleBuilder);
        }
        
        return triggerBuilder.build();
    }
    
    /**
     * Build updated trigger from existing trigger and update request
     * @param existingTrigger existing trigger
     * @param request update request
     * @return updated trigger instance
     * @throws ParseException if cron expression is invalid
     */
    private Trigger buildUpdatedTrigger(Trigger existingTrigger, UpdateTriggerRequest request) 
            throws ParseException {
        
        TriggerBuilder<? extends Trigger> triggerBuilder = existingTrigger.getTriggerBuilder();
        
        if (request.getDescription() != null) {
            triggerBuilder.withDescription(request.getDescription());
        }
        
        if (request.getPriority() != null) {
            triggerBuilder.withPriority(request.getPriority());
        }
        
        if (request.getStartTime() != null) {
            triggerBuilder.startAt(request.getStartTime());
        }
        
        if (request.getEndTime() != null) {
            triggerBuilder.endAt(request.getEndTime());
        }
        
        if (request.getCalendarName() != null) {
            triggerBuilder.modifiedByCalendar(request.getCalendarName());
        }
        
        // Handle cron expression update for CronTrigger
        if (request.getCronExpression() != null && existingTrigger instanceof CronTrigger) {
            // Create a new TriggerBuilder instead of using the existing one
            TriggerBuilder<CronTrigger> cronTriggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(existingTrigger.getKey())
                .forJob(existingTrigger.getJobKey())
                .withDescription(request.getDescription() != null ? 
                    request.getDescription() : existingTrigger.getDescription())
                .withPriority(request.getPriority() != null ? 
                    request.getPriority() : existingTrigger.getPriority())
                .startAt(request.getStartTime() != null ? 
                    request.getStartTime() : existingTrigger.getStartTime())
                .endAt(request.getEndTime() != null ? 
                    request.getEndTime() : existingTrigger.getEndTime())
                .withSchedule(
                    CronScheduleBuilder.cronSchedule(request.getCronExpression())
                        .withMisfireHandlingInstructionDoNothing()
                );
            
            if (request.getCalendarName() != null) {
                cronTriggerBuilder.modifiedByCalendar(request.getCalendarName());
            } else if (existingTrigger.getCalendarName() != null) {
                cronTriggerBuilder.modifiedByCalendar(existingTrigger.getCalendarName());
            }
            
            return cronTriggerBuilder.build();
        }
        
        return triggerBuilder.build();
    }
} 