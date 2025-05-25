package com.quartz.monitor.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.SchedulerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.quartz.monitor.exception.SchedulerNotFoundException;

/**
 * Service for managing Quartz schedulers
 */
@Service
public class SchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);
    
    private final List<Scheduler> schedulers;
    private final Scheduler defaultScheduler;
    
    public SchedulerService(List<Scheduler> schedulers, Scheduler defaultScheduler) {
        this.schedulers = schedulers;
        this.defaultScheduler = defaultScheduler;
    }
    
    /**
     * Get all active schedulers
     * @return collection of schedulers
     */
    public Collection<Scheduler> getAllSchedulers() {
        // Try to get all schedulers from SchedulerRepository
        Collection<Scheduler> allSchedulers = SchedulerRepository.getInstance().lookupAll();
        
        if (allSchedulers != null && !allSchedulers.isEmpty()) {
            logger.debug("Found {} schedulers in SchedulerRepository", allSchedulers.size());
            return allSchedulers;
        }
        
        // If no schedulers found in repository, use injected schedulers
        if (schedulers != null && !schedulers.isEmpty()) {
            logger.debug("Using {} injected schedulers", schedulers.size());
            return schedulers;
        }
        
        // Fall back to default scheduler
        logger.debug("Using default scheduler as fallback");
        List<Scheduler> defaultList = new ArrayList<>();
        defaultList.add(defaultScheduler);
        return defaultList;
    }
    
    /**
     * Get scheduler by name using SchedulerRepository lookup
     * @param schedulerName scheduler name
     * @return scheduler instance
     * @throws SchedulerNotFoundException if scheduler not found
     */
    public Scheduler getSchedulerByName(String schedulerName) {
        logger.debug("Looking up scheduler by name: {}", schedulerName);
        
        // First try direct lookup from SchedulerRepository
        Scheduler scheduler = SchedulerRepository.getInstance().lookup(schedulerName);
        if (scheduler != null) {
            logger.debug("Found scheduler {} in repository", schedulerName);
            return scheduler;
        }
        
        // If not found, try checking injected schedulers
        if (schedulers != null) {
            for (Scheduler s : schedulers) {
                try {
                    if (s.getSchedulerName().equals(schedulerName)) {
                        logger.debug("Found scheduler {} in injected schedulers", schedulerName);
                        return s;
                    }
                } catch (SchedulerException e) {
                    logger.error("Error getting scheduler name", e);
                }
            }
        }
        
        // Check default scheduler
        try {
            if (defaultScheduler.getSchedulerName().equals(schedulerName)) {
                logger.debug("Found scheduler {} as default scheduler", schedulerName);
                return defaultScheduler;
            }
        } catch (SchedulerException e) {
            logger.error("Error getting default scheduler name", e);
        }
        
        logger.error("Scheduler not found: {}", schedulerName);
        throw new SchedulerNotFoundException("Scheduler not found: " + schedulerName);
    }
    
    /**
     * Get all started schedulers
     * @return collection of started schedulers
     */
    public Collection<Scheduler> getStartedSchedulers() {
        Collection<Scheduler> allSchedulers = getAllSchedulers();
        List<Scheduler> startedSchedulers = new ArrayList<>();
        
        for (Scheduler scheduler : allSchedulers) {
            try {
                if (scheduler.isStarted()) {
                    startedSchedulers.add(scheduler);
                }
            } catch (SchedulerException e) {
                logger.error("Error checking scheduler status", e);
            }
        }
        
        logger.debug("Found {} started schedulers out of {} total", 
                     startedSchedulers.size(), allSchedulers.size());
        return startedSchedulers;
    }
} 