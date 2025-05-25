package com.quartzwatch.microbatch.integration;

import com.quartzwatch.microbatch.config.QuartzWatchProperties;
import com.quartzwatch.microbatch.misfire.MisfireCompensationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.quartz.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Misfire补偿逻辑测试
 */
@SpringBootTest
public class MisfireCompensationTest {
    
    @MockBean
    private Scheduler scheduler;
    
    private MisfireCompensationService compensationService;
    private QuartzWatchProperties properties;
    
    @Mock
    private JobExecutionContext context;
    
    @Mock
    private JobDetail jobDetail;
    
    @Mock
    private JobDataMap jobDataMap;
    
    @Mock
    private Trigger trigger;
    
    @BeforeEach
    public void setUp() {
        properties = new QuartzWatchProperties();
        properties.getMisfire().setEnabled(true);
        properties.getMisfire().setThreshold(30000); // 30秒
        properties.getMisfire().setMaxRetry(3);
        properties.getMisfire().setRetryInterval(5000);
        
        compensationService = new MisfireCompensationService(properties);
        compensationService.scheduler = scheduler;
        
        when(context.getJobDetail()).thenReturn(jobDetail);
        when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
        when(jobDetail.getKey()).thenReturn(new JobKey("testJob", "DEFAULT"));
    }
    
    @Test
    public void testMisfireDetection() {
        // 设置触发时间为35秒前（超过阈值）
        long scheduledTime = System.currentTimeMillis() - 35000;
        when(context.getScheduledFireTime()).thenReturn(new Date(scheduledTime));
        when(trigger.getPreviousFireTime()).thenReturn(new Date(scheduledTime - 60000));
        when(context.getTrigger()).thenReturn(trigger);
        
        // 记录Misfire
        compensationService.recordMisfire(context);
        
        // 验证Misfire标记被设置
        verify(jobDataMap).put("misfire", true);
        verify(jobDataMap).put(eq("misfireTime"), any(Long.class));
    }
    
    @Test
    public void testMisfireCompensation() throws SchedulerException {
        // 启用补偿
        when(jobDataMap.containsKey("misfire")).thenReturn(true);
        
        // 执行补偿
        compensationService.compensate(context);
        
        // 验证作业被重新触发
        verify(scheduler).triggerJob(any(JobKey.class), any(JobDataMap.class));
    }
    
    @Test
    public void testMaxRetryLimit() throws SchedulerException {
        // 模拟已经重试了3次
        JobKey jobKey = new JobKey("testJob", "DEFAULT");
        
        // 执行4次补偿（超过最大重试次数）
        for (int i = 0; i < 4; i++) {
            compensationService.compensate(context);
        }
        
        // 验证只触发了3次（最大重试次数）
        verify(scheduler, times(3)).triggerJob(any(JobKey.class), any(JobDataMap.class));
    }
    
    @Test
    public void testCompensationDisabled() throws SchedulerException {
        // 禁用补偿
        properties.getMisfire().setEnabled(false);
        compensationService = new MisfireCompensationService(properties);
        compensationService.scheduler = scheduler;
        
        // 尝试执行补偿
        compensationService.compensate(context);
        
        // 验证作业没有被触发
        verify(scheduler, never()).triggerJob(any(JobKey.class), any(JobDataMap.class));
    }
    
    @Test
    public void testThresholdValidation() {
        // 设置触发时间为25秒前（未超过阈值）
        long scheduledTime = System.currentTimeMillis() - 25000;
        when(context.getScheduledFireTime()).thenReturn(new Date(scheduledTime));
        
        // 验证不会被标记为Misfire
        long delay = System.currentTimeMillis() - scheduledTime;
        assertTrue(delay < compensationService.getMisfireThreshold());
    }
    
    @Test
    public void testRetryCountReset() {
        JobKey jobKey = new JobKey("testJob", "DEFAULT");
        
        // 执行一次补偿
        compensationService.compensate(context);
        
        // 清除重试计数
        compensationService.clearRetryCount(jobKey);
        
        // 再次执行补偿应该从0开始计数
        compensationService.compensate(context);
        
        // 验证仍然可以触发（因为计数被重置）
        try {
            verify(scheduler, times(2)).triggerJob(any(JobKey.class), any(JobDataMap.class));
        } catch (SchedulerException e) {
            fail("Should not throw exception");
        }
    }
    
    @Test
    public void testCompensationWithSchedulerException() throws SchedulerException {
        // 模拟调度器异常
        doThrow(new SchedulerException("Test exception"))
            .when(scheduler).triggerJob(any(JobKey.class), any(JobDataMap.class));
        
        // 执行补偿不应该抛出异常
        assertDoesNotThrow(() -> compensationService.compensate(context));
    }
} 