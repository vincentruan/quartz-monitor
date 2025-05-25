package com.quartzwatch.api.sharding;

import java.util.List;

/**
 * 分片策略SPI接口
 * 用于实现不同的任务分片算法
 * 
 * @author QuartzWatch
 */
public interface ShardingStrategy {
    
    /**
     * 分配分片给集群中的节点
     * 
     * @param totalShards 总分片数
     * @param jobKey 任务标识
     * @return 分片分配结果
     */
    List<Shard> allocateShards(int totalShards, String jobKey);
    
    /**
     * 获取策略名称
     * 
     * @return 策略名称
     */
    String getName();
} 