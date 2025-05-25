package com.quartzwatch.api;

import com.quartzwatch.microbatch.config.QuartzWatchAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用QuartzWatch自动配置
 * 
 * @author QuartzWatch
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(QuartzWatchAutoConfiguration.class)
public @interface EnableQuartzWatch {
    
    /**
     * 是否启用集群模式
     */
    boolean enableCluster() default true;
    
    /**
     * 是否启用Misfire补偿
     */
    boolean enableMisfireCompensation() default true;
    
    /**
     * 是否启用JMX监控
     */
    boolean enableJmxMonitoring() default true;
} 