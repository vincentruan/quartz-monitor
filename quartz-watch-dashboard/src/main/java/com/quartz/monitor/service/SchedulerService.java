package com.quartz.monitor.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.impl.SchedulerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public SchedulerService(List<Scheduler> schedulers, Scheduler defaultScheduler, NamedParameterJdbcTemplate jdbcTemplate) {
        this.schedulers = schedulers;
        this.defaultScheduler = defaultScheduler;
        this.jdbcTemplate = jdbcTemplate;
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

    /**
     * Get active instance count for a given scheduler name.
     * @param schedulerName the name of the scheduler
     * @return the number of active instances
     * @throws SchedulerNotFoundException if the scheduler is not found or metadata cannot be retrieved
     */
    public int getActiveInstanceCount(String schedulerName) {
        logger.debug("Getting active instance count for scheduler: {}", schedulerName);
        try {
            Scheduler scheduler = getSchedulerByName(schedulerName);
            SchedulerMetaData metaData = scheduler.getMetaData();

            if (!metaData.isJobStoreClustered()) {
                logger.warn("Scheduler {} is not clustered. Active instance count may not be accurate.", schedulerName);
                // For a non-clustered scheduler, instance count is typically 1 if running.
                return scheduler.isStarted() ? 1 : 0;
            }

            // Get cluster check-in interval from scheduler properties
            // Note: Accessing Quartz properties directly can be complex.
            // Here, we assume a common check-in interval or make it configurable if possible.
            // For simplicity, we'll rely on the fact that QRTZ_SCHEDULER_STATE updates LAST_CHECKIN_TIME.
            // A common default for clusterCheckinInterval is 15000ms (15s).
            // We'll use a threshold of 3 times a typical checkin interval (e.g., 20000ms from QuartzConfig).
            long checkinInterval = 20000; // Default or fetched from config if possible
            long thresholdPeriod = checkinInterval * 3; // Consider instances active if checked in within 3 intervals
            long currentTime = System.currentTimeMillis();
            long thresholdTime = currentTime - thresholdPeriod;

            String sql = "SELECT COUNT(DISTINCT INSTANCE_NAME) FROM QRTZ_SCHEDULER_STATE " +
                         "WHERE SCHED_NAME = :schedulerName AND LAST_CHECKIN_TIME > :thresholdTime";

            Map<String, Object> params = new HashMap<>();
            params.put("schedulerName", schedulerName);
            params.put("thresholdTime", thresholdTime);

            Integer instanceCount = jdbcTemplate.queryForObject(sql, params, Integer.class);
            logger.debug("Found {} active instances for scheduler {}", instanceCount, schedulerName);
            return instanceCount != null ? instanceCount : 0;

        } catch (SchedulerException e) {
            logger.error("Error getting scheduler metadata for {}: {}", schedulerName, e.getMessage());
            throw new SchedulerNotFoundException("Could not retrieve metadata for scheduler: " + schedulerName);
        } catch (Exception e) {
            logger.error("Error querying QRTZ_SCHEDULER_STATE for scheduler {}: {}", schedulerName, e.getMessage());
            // Fallback or rethrow as appropriate. For now, return 0 or throw custom exception.
            return 0;
        }
    }
}