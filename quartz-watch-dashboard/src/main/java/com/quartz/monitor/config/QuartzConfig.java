package com.quartz.monitor.config;

import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Quartz configuration for Spring Boot
 */
@Configuration
public class QuartzConfig {
    
    /**
     * Configure SchedulerFactoryBean with custom properties
     * This is optional - Spring Boot auto-configuration will create a default one if not provided
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, QuartzProperties quartzProperties) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        
        // Configure data source for persistent job storage
        factory.setDataSource(dataSource);
        
        // Set Quartz properties
        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());
        
        // Additional custom properties
        properties.setProperty("org.quartz.scheduler.instanceName", "QuartzMonitorScheduler");
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        properties.setProperty("org.quartz.threadPool.threadCount", "10");
        properties.setProperty("org.quartz.jobStore.isClustered", "true");
        properties.setProperty("org.quartz.jobStore.clusterCheckinInterval", "20000");
        
        factory.setQuartzProperties(properties);
        
        // Optional: Set to true if you want to update existing jobs
        factory.setOverwriteExistingJobs(true);
        
        // Optional: Set startup delay
        factory.setStartupDelay(10);
        
        // Optional: Auto startup
        factory.setAutoStartup(true);
        
        return factory;
    }
} 