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

import com.quartz.monitor.dto.PageResult;
import com.quartz.monitor.dto.TriggerInfo;
import com.quartz.monitor.dto.request.CreateTriggerRequest;
import com.quartz.monitor.dto.request.UpdateTriggerRequest;
import com.quartz.monitor.dto.response.TriggerDetailResponse;
import com.quartz.monitor.service.TriggerService;

/**
 * REST controller for managing Quartz triggers
 * Supports multiple Scheduler instances
 */
@RestController
@RequestMapping("/api/triggers")
@Validated
public class TriggerController {

    private static final Logger logger = LoggerFactory.getLogger(TriggerController.class);
    
    private final TriggerService triggerService;
    
    public TriggerController(TriggerService triggerService) {
        this.triggerService = triggerService;
    }
    
    /**
     * Get triggers for a specific job
     * @param schedulerName scheduler name
     * @param jobGroup job group
     * @param jobName job name
     * @param page page number (0-based)
     * @param size items per page
     * @return paginated trigger list
     */
    @GetMapping("/job/{schedulerName}/{jobGroup}/{jobName}")
    public PageResult<TriggerInfo> getTriggersForJob(
            @PathVariable @NotBlank String schedulerName,
            @PathVariable @NotBlank String jobGroup,
            @PathVariable @NotBlank String jobName,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        logger.debug("Getting triggers for job - scheduler: {}, group: {}, name: {}", 
                     schedulerName, jobGroup, jobName);
        
        return triggerService.getTriggersForJob(schedulerName, jobGroup, jobName, page, size);
    }
    
    /**
     * Get trigger details
     * @param schedulerName scheduler name
     * @param triggerGroup trigger group
     * @param triggerName trigger name
     * @return trigger details
     */
    @GetMapping("/{schedulerName}/{triggerGroup}/{triggerName}")
    public TriggerDetailResponse getTriggerDetail(
            @PathVariable @NotBlank String schedulerName,
            @PathVariable @NotBlank String triggerGroup,
            @PathVariable @NotBlank String triggerName) {
        
        logger.debug("Getting trigger details - scheduler: {}, group: {}, name: {}", 
                     schedulerName, triggerGroup, triggerName);
        
        return triggerService.getTriggerDetail(schedulerName, triggerGroup, triggerName);
    }
    
    /**
     * Create a new trigger
     * @param request trigger creation request
     * @return created trigger info
     */
    @PostMapping
    public TriggerInfo createTrigger(@RequestBody @Valid CreateTriggerRequest request) {
        logger.info("Creating trigger - scheduler: {}, group: {}, name: {}", 
                    request.getSchedulerName(), request.getTriggerGroup(), request.getTriggerName());
        
        return triggerService.createTrigger(request);
    }
    
    /**
     * Update an existing trigger
     * @param schedulerName scheduler name
     * @param triggerGroup trigger group
     * @param triggerName trigger name
     * @param request update request
     * @return updated trigger info
     */
    @PutMapping("/{schedulerName}/{triggerGroup}/{triggerName}")
    public TriggerInfo updateTrigger(
            @PathVariable @NotBlank String schedulerName,
            @PathVariable @NotBlank String triggerGroup,
            @PathVariable @NotBlank String triggerName,
            @RequestBody @Valid UpdateTriggerRequest request) {
        
        logger.info("Updating trigger - scheduler: {}, group: {}, name: {}", 
                    schedulerName, triggerGroup, triggerName);
        
        return triggerService.updateTrigger(schedulerName, triggerGroup, triggerName, request);
    }
    
    /**
     * Delete a trigger
     * @param schedulerName scheduler name
     * @param triggerGroup trigger group
     * @param triggerName trigger name
     */
    @DeleteMapping("/{schedulerName}/{triggerGroup}/{triggerName}")
    public void deleteTrigger(
            @PathVariable @NotBlank String schedulerName,
            @PathVariable @NotBlank String triggerGroup,
            @PathVariable @NotBlank String triggerName) {
        
        logger.info("Deleting trigger - scheduler: {}, group: {}, name: {}", 
                    schedulerName, triggerGroup, triggerName);
        
        triggerService.deleteTrigger(schedulerName, triggerGroup, triggerName);
    }
    
    /**
     * Pause a trigger
     * @param schedulerName scheduler name
     * @param triggerGroup trigger group
     * @param triggerName trigger name
     */
    @PostMapping("/{schedulerName}/{triggerGroup}/{triggerName}/pause")
    public void pauseTrigger(
            @PathVariable @NotBlank String schedulerName,
            @PathVariable @NotBlank String triggerGroup,
            @PathVariable @NotBlank String triggerName) {
        
        logger.info("Pausing trigger - scheduler: {}, group: {}, name: {}", 
                    schedulerName, triggerGroup, triggerName);
        
        triggerService.pauseTrigger(schedulerName, triggerGroup, triggerName);
    }
    
    /**
     * Resume a paused trigger
     * @param schedulerName scheduler name
     * @param triggerGroup trigger group
     * @param triggerName trigger name
     */
    @PostMapping("/{schedulerName}/{triggerGroup}/{triggerName}/resume")
    public void resumeTrigger(
            @PathVariable @NotBlank String schedulerName,
            @PathVariable @NotBlank String triggerGroup,
            @PathVariable @NotBlank String triggerName) {
        
        logger.info("Resuming trigger - scheduler: {}, group: {}, name: {}", 
                    schedulerName, triggerGroup, triggerName);
        
        triggerService.resumeTrigger(schedulerName, triggerGroup, triggerName);
    }
} 