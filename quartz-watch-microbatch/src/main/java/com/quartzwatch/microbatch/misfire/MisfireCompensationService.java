package com.quartzwatch.microbatch.misfire;

import com.quartzwatch.api.Internal;
import com.quartzwatch.microbatch.config.QuartzWatchProperties;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Misfire补偿服务
 * 
 * @author QuartzWatch
 */
@Slf4j
@Service
@Internal
public class MisfireCompensationService {
    
    private final QuartzWatchProperties properties;
    private final Map<JobKey, AtomicInteger> retryCountMap = new ConcurrentHashMap<>();
    
    @Autowired
    private Scheduler scheduler;
    
    public MisfireCompensationService(QuartzWatchProperties properties) {
        this.properties = properties;
    }
    
    public boolean isMisfireCompensationEnabled() {
        return properties.getMisfire().isEnabled();
    }
    
    public long getMisfireThreshold() {
        return properties.getMisfire().getThreshold();
    }
    
    /**
     * 记录Misfire事件
     */
    public void recordMisfire(JobExecutionContext context) {
        JobKey jobKey = context.getJobDetail().getKey();
        context.getJobDetail().getJobDataMap().put("misfire", true);
        context.getJobDetail().getJobDataMap().put("misfireTime", System.currentTimeMillis());
        
        log.info("Recorded misfire for job: {}", jobKey);
    }
    
    /**
     * 执行补偿
     */
    public void compensate(JobExecutionContext context) {
        if (!properties.getMisfire().isEnabled()) {
            return;
        }
        
        JobKey jobKey = context.getJobDetail().getKey();
        AtomicInteger retryCount = retryCountMap.computeIfAbsent(jobKey, k -> new AtomicInteger(0));
        
        if (retryCount.get() >= properties.getMisfire().getMaxRetry()) {
            log.error("Max retry count reached for job: {}, giving up compensation", jobKey);
            retryCountMap.remove(jobKey);
            return;
        }
        
        try {
            // 延迟后重新触发作业
            long delay = properties.getMisfire().getRetryInterval();
            Date triggerTime = new Date(System.currentTimeMillis() + delay);
            
            scheduler.triggerJob(jobKey, context.getJobDetail().getJobDataMap());
            
            retryCount.incrementAndGet();
            log.info("Scheduled compensation for job: {}, retry: {}/{}", 
                    jobKey, retryCount.get(), properties.getMisfire().getMaxRetry());
            
        } catch (SchedulerException e) {
            log.error("Failed to schedule compensation for job: {}", jobKey, e);
        }
    }
    
    /**
     * 清除作业的重试计数
     */
    public void clearRetryCount(JobKey jobKey) {
        retryCountMap.remove(jobKey);
    }
} 