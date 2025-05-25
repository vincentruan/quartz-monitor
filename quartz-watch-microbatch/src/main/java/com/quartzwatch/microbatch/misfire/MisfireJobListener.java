package com.quartzwatch.microbatch.misfire;

import com.quartzwatch.api.Internal;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * Misfire作业监听器
 * 监听作业执行中的Misfire事件
 * 
 * @author QuartzWatch
 */
@Slf4j
@Internal
public class MisfireJobListener implements JobListener {
    
    private final MisfireCompensationService compensationService;
    
    public MisfireJobListener(MisfireCompensationService compensationService) {
        this.compensationService = compensationService;
    }
    
    @Override
    public String getName() {
        return "QuartzWatchMisfireListener";
    }
    
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        // 检查是否是Misfire触发
        if (context.getTrigger().getPreviousFireTime() != null) {
            long scheduledTime = context.getScheduledFireTime().getTime();
            long actualTime = System.currentTimeMillis();
            long delay = actualTime - scheduledTime;
            
            // 如果延迟超过阈值，记录Misfire事件
            if (delay > compensationService.getMisfireThreshold()) {
                log.warn("Job misfire detected - Job: {}, Delay: {}ms", 
                        context.getJobDetail().getKey(), delay);
                compensationService.recordMisfire(context);
            }
        }
    }
    
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.info("Job execution vetoed: {}", context.getJobDetail().getKey());
    }
    
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (jobException != null) {
            log.error("Job execution failed: {}", context.getJobDetail().getKey(), jobException);
            
            // 如果是Misfire引起的执行失败，尝试补偿
            if (compensationService.isMisfireCompensationEnabled() && 
                context.getJobDetail().getJobDataMap().containsKey("misfire")) {
                compensationService.compensate(context);
            }
        }
    }
} 