package com.quartzwatch.microbatch.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * QuartzWatch配置属性
 * 
 * @author QuartzWatch
 */
@Data
@ConfigurationProperties(prefix = "quartz.watch")
public class QuartzWatchProperties {
    
    /**
     * 节点配置
     */
    private Node node = new Node();
    
    /**
     * 集群配置
     */
    private Cluster cluster = new Cluster();
    
    /**
     * Misfire配置
     */
    private Misfire misfire = new Misfire();
    
    /**
     * 监控配置
     */
    private Monitor monitor = new Monitor();
    
    @Data
    public static class Node {
        /**
         * 节点ID，默认为hostname:port
         */
        private String id;
        
        /**
         * 心跳间隔（毫秒）
         */
        private long heartbeatInterval = 5000;
        
        /**
         * 节点超时时间（毫秒）
         */
        private long timeout = 30000;
    }
    
    @Data
    public static class Cluster {
        /**
         * 是否启用集群模式
         */
        private boolean enabled = true;
        
        /**
         * 默认分片策略
         */
        private String defaultShardingStrategy = "round-robin";
    }
    
    @Data
    public static class Misfire {
        /**
         * 是否启用Misfire补偿
         */
        private boolean enabled = true;
        
        /**
         * 补偿阈值（毫秒）
         */
        private long threshold = 30000;
        
        /**
         * 最大重试次数
         */
        private int maxRetry = 3;
        
        /**
         * 重试间隔（毫秒）
         */
        private long retryInterval = 5000;
    }
    
    @Data
    public static class Monitor {
        /**
         * 是否启用JMX监控
         */
        private boolean jmxEnabled = true;
        
        /**
         * 监控端点前缀
         */
        private String endpointPrefix = "/quartzwatch";
    }
} 