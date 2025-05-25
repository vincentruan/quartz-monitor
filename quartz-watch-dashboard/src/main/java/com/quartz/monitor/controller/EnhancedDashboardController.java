package com.quartz.monitor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quartz.monitor.dto.response.DashboardStatsResponse;
import com.quartz.monitor.dto.response.RecentJobsResponse;
import com.quartz.monitor.service.DashboardService;

/**
 * Enhanced REST controller for dashboard operations
 * Supports multiple Scheduler instances
 */
@RestController
@RequestMapping("/api/dashboard/v2")
@Validated
public class EnhancedDashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedDashboardController.class);
    
    private final DashboardService dashboardService;
    
    public EnhancedDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    /**
     * Get system statistics including instance, scheduler, job, and trigger counts
     * @return dashboard statistics response
     */
    @GetMapping("/stats")
    public DashboardStatsResponse getStats() {
        logger.debug("Getting dashboard statistics");
        return dashboardService.getStats();
    }
    
    /**
     * Get recently executed or scheduled jobs from all schedulers
     * @return recent jobs response
     */
    @GetMapping("/recent-jobs")
    public RecentJobsResponse getRecentJobs() {
        logger.debug("Getting recent jobs");
        return dashboardService.getRecentJobs();
    }
} 