package com.quartzwatch.api.registry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 节点元数据
 * 
 * @author QuartzWatch
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeMetadata {
    
    /**
     * 主机名
     */
    private String hostname;
    
    /**
     * 端口号
     */
    private int port;
    
    /**
     * 应用名称
     */
    private String appName;
    
    /**
     * 版本号
     */
    private String version;
    
    /**
     * 启动时间
     */
    private long startTime;
    
    /**
     * 自定义元数据
     */
    private Map<String, Object> customMetadata;
} 