package com.quartzwatch.api.registry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 节点信息
 * 
 * @author QuartzWatch
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeInfo {
    
    /**
     * 节点ID
     */
    private String nodeId;
    
    /**
     * 节点元数据
     */
    private NodeMetadata metadata;
    
    /**
     * 最后心跳时间
     */
    private long lastHeartbeat;
    
    /**
     * 节点状态
     */
    private NodeStatus status;
    
    /**
     * 分配的分片数
     */
    private int[] assignedShards;
    
    public enum NodeStatus {
        ACTIVE,
        INACTIVE,
        FAILED
    }
} 