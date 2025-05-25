package com.quartzwatch.api.sharding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 分片信息
 * 
 * @author QuartzWatch
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shard {
    
    /**
     * 节点ID（格式：host:port）
     */
    private String nodeId;
    
    /**
     * 分片编号列表
     */
    private int[] shardNumbers;
    
    /**
     * 节点元数据
     */
    private Map<String, Object> metadata;
} 