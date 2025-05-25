package com.quartzwatch.microbatch.cluster;

import com.quartzwatch.api.Internal;
import com.quartzwatch.api.registry.NodeInfo;
import com.quartzwatch.api.sharding.Shard;
import com.quartzwatch.api.sharding.ShardingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 轮询分片策略
 * 
 * @author QuartzWatch
 */
@Slf4j
@Internal
public class RoundRobinShardingStrategy implements ShardingStrategy {
    
    private NodeSelector nodeSelector;
    
    public void setNodeSelector(NodeSelector nodeSelector) {
        this.nodeSelector = nodeSelector;
    }
    
    @Override
    public List<Shard> allocateShards(int totalShards, String jobKey) {
        if (nodeSelector == null) {
            log.error("NodeSelector not initialized");
            return Collections.emptyList();
        }
        
        List<NodeInfo> activeNodes = nodeSelector.selectNodesForJob(jobKey);
        
        if (activeNodes.isEmpty()) {
            log.warn("No active nodes available for job: {}", jobKey);
            return Collections.emptyList();
        }
        
        List<Shard> shards = new ArrayList<>();
        Map<String, List<Integer>> nodeShardMap = new HashMap<>();
        
        // 初始化每个节点的分片列表
        for (NodeInfo node : activeNodes) {
            nodeShardMap.put(node.getNodeId(), new ArrayList<>());
        }
        
        // 轮询分配分片
        for (int i = 0; i < totalShards; i++) {
            NodeInfo node = activeNodes.get(i % activeNodes.size());
            nodeShardMap.get(node.getNodeId()).add(i);
        }
        
        // 构建分片结果
        for (Map.Entry<String, List<Integer>> entry : nodeShardMap.entrySet()) {
            NodeInfo nodeInfo = activeNodes.stream()
                    .filter(n -> n.getNodeId().equals(entry.getKey()))
                    .findFirst()
                    .orElse(null);
                    
            if (nodeInfo != null) {
                Shard shard = new Shard();
                shard.setNodeId(entry.getKey());
                shard.setShardNumbers(entry.getValue().stream().mapToInt(Integer::intValue).toArray());
                shard.setMetadata(nodeInfo.getMetadata().getCustomMetadata());
                shards.add(shard);
            }
        }
        
        log.info("Allocated {} shards to {} nodes using round-robin strategy for job: {}", 
                 totalShards, activeNodes.size(), jobKey);
        
        return shards;
    }
    
    @Override
    public String getName() {
        return "round-robin";
    }
} 