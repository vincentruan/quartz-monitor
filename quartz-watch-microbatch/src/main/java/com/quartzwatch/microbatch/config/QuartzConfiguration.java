package com.quartzwatch.microbatch.config;

import com.quartzwatch.api.Internal;
import com.quartzwatch.microbatch.misfire.MisfireJobListener;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Quartz核心配置
 * 
 * @author QuartzWatch
 */
@Configuration
@Internal
@EnableConfigurationProperties(QuartzProperties.class)
public class QuartzConfiguration {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private QuartzWatchProperties quartzWatchProperties;
    
    @Autowired
    private MisfireJobListener misfireJobListener;
    
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }
    
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory,
                                                     QuartzProperties quartzProperties) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        factory.setDataSource(dataSource);
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true);
        factory.setStartupDelay(5);
        
        // 设置Quartz属性
        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());
        
        // 集群配置
        if (quartzWatchProperties.getCluster().isEnabled()) {
            properties.setProperty("org.quartz.jobStore.isClustered", "true");
            properties.setProperty("org.quartz.jobStore.clusterCheckinInterval", "20000");
        }
        
        // 基本配置
        properties.setProperty("org.quartz.scheduler.instanceName", "QuartzWatchScheduler");
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        properties.setProperty("org.quartz.threadPool.threadCount", "10");
        
        // 持久化配置
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        properties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
        properties.setProperty("org.quartz.jobStore.useProperties", "false");
        
        factory.setQuartzProperties(properties);
        
        // 添加全局Job监听器
        factory.setGlobalJobListeners(misfireJobListener);
        
        return factory;
    }
} 