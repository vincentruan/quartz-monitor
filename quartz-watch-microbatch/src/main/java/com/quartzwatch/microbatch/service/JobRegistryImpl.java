package com.quartzwatch.microbatch.service;

import com.quartzwatch.api.Internal;
import com.quartzwatch.api.JobInfo;
import com.quartzwatch.api.JobRegistry;
import com.quartzwatch.api.sharding.Shard;
import com.quartzwatch.api.sharding.ShardingStrategy;
import com.quartzwatch.microbatch.config.QuartzWatchProperties;
import com.quartzwatch.microbatch.registry.NodeRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 作业注册服务实现
 * 
 * @author QuartzWatch
 */
@Slf4j
@Service
@Internal
public class JobRegistryImpl implements JobRegistry {
    
    private final NodeRegistryService nodeRegistryService;
    private final Map<String, ShardingStrategy> shardingStrategies;
    
    @Autowired
    private Scheduler scheduler;
    
    @Autowired
    private QuartzWatchProperties properties;
    
    public JobRegistryImpl(NodeRegistryService nodeRegistryService,
                          Map<String, ShardingStrategy> shardingStrategies) {
        this.nodeRegistryService = nodeRegistryService;
        this.shardingStrategies = shardingStrategies;
    }
    
    @Override
    public boolean registerJob(JobDetail jobDetail, Trigger trigger, int totalShards) {
        return registerJob(jobDetail, trigger, totalShards, 
                          properties.getCluster().getDefaultShardingStrategy());
    }
    
    @Override
    public boolean registerJob(JobDetail jobDetail, Trigger trigger, int totalShards, 
                              String shardingStrategyName) {
        try {
            // 获取分片策略
            ShardingStrategy strategy = shardingStrategies.get(shardingStrategyName);
            if (strategy == null) {
                log.error("Sharding strategy not found: {}", shardingStrategyName);
                return false;
            }
            
            // 设置作业数据
            jobDetail.getJobDataMap().put("totalShards", totalShards);
            jobDetail.getJobDataMap().put("shardingStrategy", shardingStrategyName);
            
            // 注册作业到Quartz
            scheduler.scheduleJob(jobDetail, trigger);
            
            // 分配分片
            String jobKey = jobDetail.getKey().toString();
            List<Shard> shards = strategy.allocateShards(totalShards, jobKey);
            nodeRegistryService.getRegistryStore().saveShardAllocation(jobKey, shards);
            
            log.info("Job registered successfully: {}, totalShards: {}, strategy: {}", 
                    jobKey, totalShards, shardingStrategyName);
            return true;
            
        } catch (SchedulerException e) {
            log.error("Failed to register job: {}", jobDetail.getKey(), e);
            return false;
        }
    }
    
    @Override
    public boolean unregisterJob(String jobKey) {
        try {
            JobKey key = parseJobKey(jobKey);
            boolean deleted = scheduler.deleteJob(key);
            
            if (deleted) {
                // 清理分片分配信息
                nodeRegistryService.getRegistryStore().saveShardAllocation(jobKey, Collections.emptyList());
                log.info("Job unregistered successfully: {}", jobKey);
            }
            
            return deleted;
        } catch (SchedulerException e) {
            log.error("Failed to unregister job: {}", jobKey, e);
            return false;
        }
    }
    
    @Override
    public boolean pauseJob(String jobKey) {
        try {
            JobKey key = parseJobKey(jobKey);
            scheduler.pauseJob(key);
            log.info("Job paused successfully: {}", jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to pause job: {}", jobKey, e);
            return false;
        }
    }
    
    @Override
    public boolean resumeJob(String jobKey) {
        try {
            JobKey key = parseJobKey(jobKey);
            scheduler.resumeJob(key);
            log.info("Job resumed successfully: {}", jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to resume job: {}", jobKey, e);
            return false;
        }
    }
    
    @Override
    public boolean triggerJob(String jobKey) {
        try {
            JobKey key = parseJobKey(jobKey);
            scheduler.triggerJob(key);
            log.info("Job triggered successfully: {}", jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to trigger job: {}", jobKey, e);
            return false;
        }
    }
    
    @Override
    public List<JobInfo> getAllJobs() {
        List<JobInfo> jobs = new ArrayList<>();
        
        try {
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    JobInfo jobInfo = getJobInfo(jobKey.toString());
                    if (jobInfo != null) {
                        jobs.add(jobInfo);
                    }
                }
            }
        } catch (SchedulerException e) {
            log.error("Failed to get all jobs", e);
        }
        
        return jobs;
    }
    
    @Override
    public JobInfo getJobInfo(String jobKey) {
        try {
            JobKey key = parseJobKey(jobKey);
            JobDetail jobDetail = scheduler.getJobDetail(key);
            
            if (jobDetail == null) {
                return null;
            }
            
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(key);
            Trigger trigger = triggers.isEmpty() ? null : triggers.get(0);
            
            JobInfo.JobInfoBuilder builder = JobInfo.builder()
                    .jobKey(jobKey)
                    .jobGroup(key.getGroup())
                    .jobClassName(jobDetail.getJobClass().getName())
                    .description(jobDetail.getDescription())
                    .totalShards(jobDetail.getJobDataMap().getInt("totalShards"))
                    .shardingStrategy(jobDetail.getJobDataMap().getString("shardingStrategy"))
                    .jobData(jobDetail.getJobDataMap().getWrappedMap());
            
            if (trigger != null) {
                builder.nextFireTime(trigger.getNextFireTime())
                       .previousFireTime(trigger.getPreviousFireTime());
                
                if (trigger instanceof CronTrigger) {
                    builder.cronExpression(((CronTrigger) trigger).getCronExpression());
                }
                
                // 设置作业状态
                Trigger.TriggerState state = scheduler.getTriggerState(trigger.getKey());
                builder.status(convertTriggerState(state));
            }
            
            return builder.build();
            
        } catch (SchedulerException e) {
            log.error("Failed to get job info: {}", jobKey, e);
            return null;
        }
    }
    
    private JobKey parseJobKey(String jobKey) {
        String[] parts = jobKey.split("\\.");
        if (parts.length == 2) {
            return new JobKey(parts[1], parts[0]);
        } else {
            return new JobKey(jobKey, "DEFAULT");
        }
    }
    
    private JobInfo.JobStatus convertTriggerState(Trigger.TriggerState state) {
        switch (state) {
            case NORMAL:
                return JobInfo.JobStatus.NORMAL;
            case PAUSED:
                return JobInfo.JobStatus.PAUSED;
            case COMPLETE:
                return JobInfo.JobStatus.COMPLETE;
            case ERROR:
                return JobInfo.JobStatus.ERROR;
            case BLOCKED:
                return JobInfo.JobStatus.BLOCKED;
            default:
                return JobInfo.JobStatus.NORMAL;
        }
    }
} 