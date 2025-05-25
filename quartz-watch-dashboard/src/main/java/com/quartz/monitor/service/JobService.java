package com.quartz.monitor.service;

import com.quartz.monitor.dto.JobInfo;
import com.quartz.monitor.dto.PageResult;
import com.quartz.monitor.dto.request.CreateJobRequest;
import com.quartz.monitor.dto.request.UpdateJobRequest;
import com.quartz.monitor.dto.response.JobDetailResponse;

/**
 * Job服务接口
 */
public interface JobService {
    
    /**
     * 获取分页的Job列表
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param schedulerName 调度器名称（可选）
     * @param groupName 组名称（可选）
     * @return 分页结果
     */
    PageResult<JobInfo> getJobs(int page, int size, String schedulerName, String groupName);
    
    /**
     * 获取Job详情
     * @param schedulerName 调度器名称
     * @param jobGroup Job组
     * @param jobName Job名称
     * @return Job详情
     */
    JobDetailResponse getJobDetail(String schedulerName, String jobGroup, String jobName);
    
    /**
     * 创建新Job
     * @param request 创建请求
     * @return 创建的Job信息
     */
    JobInfo createJob(CreateJobRequest request);
    
    /**
     * 更新Job
     * @param schedulerName 调度器名称
     * @param jobGroup Job组
     * @param jobName Job名称
     * @param request 更新请求
     * @return 更新后的Job信息
     */
    JobInfo updateJob(String schedulerName, String jobGroup, String jobName, UpdateJobRequest request);
    
    /**
     * 删除Job
     * @param schedulerName 调度器名称
     * @param jobGroup Job组
     * @param jobName Job名称
     */
    void deleteJob(String schedulerName, String jobGroup, String jobName);
    
    /**
     * 暂停Job
     * @param schedulerName 调度器名称
     * @param jobGroup Job组
     * @param jobName Job名称
     */
    void pauseJob(String schedulerName, String jobGroup, String jobName);
    
    /**
     * 恢复Job
     * @param schedulerName 调度器名称
     * @param jobGroup Job组
     * @param jobName Job名称
     */
    void resumeJob(String schedulerName, String jobGroup, String jobName);
    
    /**
     * 立即触发Job
     * @param schedulerName 调度器名称
     * @param jobGroup Job组
     * @param jobName Job名称
     */
    void triggerJob(String schedulerName, String jobGroup, String jobName);
} 