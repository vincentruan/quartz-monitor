package com.quartz.monitor.service;

import com.quartz.monitor.dto.response.DashboardStatsResponse;
import com.quartz.monitor.dto.response.RecentJobsResponse;

/**
 * Dashboard服务接口
 */
public interface DashboardService {
    
    /**
     * 获取系统统计信息
     * @return 统计信息响应
     */
    DashboardStatsResponse getStats();
    
    /**
     * 获取最近的任务列表
     * @return 最近任务响应
     */
    RecentJobsResponse getRecentJobs();
} 