package com.quartzwatch.microbatch.registry;

import com.quartzwatch.api.Internal;
import com.quartzwatch.api.registry.NodeMetadata;
import com.quartzwatch.api.registry.RegistryStore;
import com.quartzwatch.microbatch.config.QuartzWatchProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 节点注册服务
 * 负责节点的注册、心跳和注销
 * 
 * @author QuartzWatch
 */
@Slf4j
@Service
@Internal
public class NodeRegistryService {
    
    private final RegistryStore registryStore;
    private final QuartzWatchProperties properties;
    private String nodeId;
    
    @Value("${server.port:8080}")
    private int serverPort;
    
    @Value("${spring.application.name:quartz-watch}")
    private String appName;
    
    @Value("${spring.application.version:1.0.0}")
    private String version;
    
    public NodeRegistryService(RegistryStore registryStore, QuartzWatchProperties properties) {
        this.registryStore = registryStore;
        this.properties = properties;
    }
    
    @PostConstruct
    public void init() {
        try {
            // 生成节点ID
            if (properties.getNode().getId() != null) {
                nodeId = properties.getNode().getId();
            } else {
                String hostname = InetAddress.getLocalHost().getHostName();
                nodeId = hostname + ":" + serverPort;
            }
            
            // 创建节点元数据
            Map<String, Object> customMetadata = new HashMap<>();
            customMetadata.put("clustered", properties.getCluster().isEnabled());
            customMetadata.put("misfireEnabled", properties.getMisfire().isEnabled());
            
            NodeMetadata metadata = NodeMetadata.builder()
                    .hostname(InetAddress.getLocalHost().getHostName())
                    .port(serverPort)
                    .appName(appName)
                    .version(version)
                    .startTime(System.currentTimeMillis())
                    .customMetadata(customMetadata)
                    .build();
            
            // 注册节点
            registryStore.registerNode(nodeId, metadata);
            log.info("Node registered successfully: {}", nodeId);
            
        } catch (Exception e) {
            log.error("Failed to register node", e);
            throw new RuntimeException("Failed to register node", e);
        }
    }
    
    @Scheduled(fixedDelayString = "${quartz.watch.node.heartbeat-interval:5000}")
    public void heartbeat() {
        try {
            registryStore.heartbeat(nodeId);
            log.debug("Heartbeat sent for node: {}", nodeId);
        } catch (Exception e) {
            log.error("Failed to send heartbeat for node: {}", nodeId, e);
        }
    }
    
    @PreDestroy
    public void destroy() {
        try {
            registryStore.unregisterNode(nodeId);
            log.info("Node unregistered successfully: {}", nodeId);
        } catch (Exception e) {
            log.error("Failed to unregister node: {}", nodeId, e);
        }
    }
    
    public String getNodeId() {
        return nodeId;
    }
    
    public RegistryStore getRegistryStore() {
        return registryStore;
    }
} 