package com.quartzwatch.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

/**
 * 作业信息
 * 
 * @author QuartzWatch
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobInfo {
    
    /**
     * 作业标识
     */
    private String jobKey;
    
    /**
     * 作业组
     */
    private String jobGroup;
    
    /**
     * 作业类名
     */
    private String jobClassName;
    
    /**
     * 作业描述
     */
    private String description;
    
    /**
     * 触发器表达式
     */
    private String cronExpression;
    
    /**
     * 总分片数
     */
    private int totalShards;
    
    /**
     * 分片策略
     */
    private String shardingStrategy;
    
    /**
     * 作业状态
     */
    private JobStatus status;
    
    /**
     * 下次触发时间
     */
    private Date nextFireTime;
    
    /**
     * 上次触发时间
     */
    private Date previousFireTime;
    
    /**
     * 作业数据
     */
    private Map<String, Object> jobData;
    
    public enum JobStatus {
        NORMAL,
        PAUSED,
        COMPLETE,
        ERROR,
        BLOCKED
    }
} 