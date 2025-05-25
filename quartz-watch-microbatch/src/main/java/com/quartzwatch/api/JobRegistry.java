package com.quartzwatch.api;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.util.List;

/**
 * 作业注册门面类
 * 提供统一的作业管理接口
 * 
 * @author QuartzWatch
 */
public interface JobRegistry {
    
    /**
     * 注册作业
     * 
     * @param jobDetail 作业详情
     * @param trigger 触发器
     * @param totalShards 总分片数
     * @return 是否注册成功
     */
    boolean registerJob(JobDetail jobDetail, Trigger trigger, int totalShards);
    
    /**
     * 注册作业（带分片策略）
     * 
     * @param jobDetail 作业详情
     * @param trigger 触发器
     * @param totalShards 总分片数
     * @param shardingStrategyName 分片策略名称
     * @return 是否注册成功
     */
    boolean registerJob(JobDetail jobDetail, Trigger trigger, int totalShards, String shardingStrategyName);
    
    /**
     * 注销作业
     * 
     * @param jobKey 作业标识
     * @return 是否注销成功
     */
    boolean unregisterJob(String jobKey);
    
    /**
     * 暂停作业
     * 
     * @param jobKey 作业标识
     * @return 是否暂停成功
     */
    boolean pauseJob(String jobKey);
    
    /**
     * 恢复作业
     * 
     * @param jobKey 作业标识
     * @return 是否恢复成功
     */
    boolean resumeJob(String jobKey);
    
    /**
     * 手动触发作业
     * 
     * @param jobKey 作业标识
     * @return 是否触发成功
     */
    boolean triggerJob(String jobKey);
    
    /**
     * 获取所有注册的作业
     * 
     * @return 作业列表
     */
    List<JobInfo> getAllJobs();
    
    /**
     * 获取作业信息
     * 
     * @param jobKey 作业标识
     * @return 作业信息
     */
    JobInfo getJobInfo(String jobKey);
} 