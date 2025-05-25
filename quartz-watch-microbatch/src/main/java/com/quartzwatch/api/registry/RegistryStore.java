package com.quartzwatch.api.registry;

import com.quartzwatch.api.sharding.Shard;

import java.util.List;

/**
 * 注册存储扩展点
 * 支持不同的存储实现（数据库、Redis、Zookeeper等）
 * 
 * @author QuartzWatch
 */
public interface RegistryStore {
    
    /**
     * 注册节点
     * 
     * @param nodeId 节点ID
     * @param metadata 节点元数据
     */
    void registerNode(String nodeId, NodeMetadata metadata);
    
    /**
     * 更新节点心跳
     * 
     * @param nodeId 节点ID
     */
    void heartbeat(String nodeId);
    
    /**
     * 注销节点
     * 
     * @param nodeId 节点ID
     */
    void unregisterNode(String nodeId);
    
    /**
     * 获取指定任务的所有节点
     * 
     * @param jobKey 任务标识
     * @return 节点列表
     */
    List<NodeInfo> getNodesForJob(String jobKey);
    
    /**
     * 获取所有活跃节点
     * 
     * @return 活跃节点列表
     */
    List<NodeInfo> getActiveNodes();
    
    /**
     * 保存分片分配结果
     * 
     * @param jobKey 任务标识
     * @param shards 分片分配结果
     */
    void saveShardAllocation(String jobKey, List<Shard> shards);
    
    /**
     * 获取分片分配结果
     * 
     * @param jobKey 任务标识
     * @return 分片分配结果
     */
    List<Shard> getShardAllocation(String jobKey);
} 