package com.quartzwatch.microbatch.cluster;

import com.quartzwatch.api.Internal;
import com.quartzwatch.api.registry.NodeInfo;
import com.quartzwatch.api.registry.RegistryStore;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 节点选择器
 * 负责选择可用于执行任务的节点
 * 
 * @author QuartzWatch
 */
@Internal
public class NodeSelector {
    
    private final RegistryStore registryStore;
    
    public NodeSelector(RegistryStore registryStore) {
        this.registryStore = registryStore;
    }
    
    /**
     * 选择可以执行指定任务的节点
     * 
     * @param jobKey 任务标识
     * @return 可用节点列表
     */
    public List<NodeInfo> selectNodesForJob(String jobKey) {
        // 获取所有活跃节点
        List<NodeInfo> activeNodes = registryStore.getActiveNodes();
        
        // 过滤出状态正常的节点
        return activeNodes.stream()
                .filter(node -> node.getStatus() == NodeInfo.NodeStatus.ACTIVE)
                .collect(Collectors.toList());
    }
} 