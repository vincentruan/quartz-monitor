package com.quartzwatch.microbatch.controller;

import com.quartzwatch.api.JobInfo;
import com.quartzwatch.api.JobRegistry;
import com.quartzwatch.api.registry.NodeInfo;
import com.quartzwatch.microbatch.registry.NodeRegistryService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * QuartzWatch REST控制器
 * 
 * @author QuartzWatch
 */
@Slf4j
@RestController
@RequestMapping("${quartz.watch.monitor.endpoint-prefix:/quartzwatch}")
public class QuartzWatchController {
    
    @Autowired
    private JobRegistry jobRegistry;
    
    @Autowired
    private NodeRegistryService nodeRegistryService;
    
    /**
     * 获取指定作业的节点信息
     */
    @GetMapping("/registry/nodes")
    public ResponseEntity<NodeRegistryResponse> getNodesForJob(@RequestParam String jobKey) {
        List<NodeInfo> nodes = nodeRegistryService.getRegistryStore().getNodesForJob(jobKey);
        
        List<NodeRegistryResponse.NodeDetail> nodeDetails = nodes.stream()
                .map(node -> {
                    NodeRegistryResponse.NodeDetail detail = new NodeRegistryResponse.NodeDetail();
                    detail.setNodeId(node.getNodeId());
                    detail.setShards(node.getAssignedShards());
                    detail.setMetadata(node.getMetadata().getCustomMetadata());
                    detail.setStatus(node.getStatus().name());
                    detail.setLastHeartbeat(node.getLastHeartbeat());
                    return detail;
                })
                .collect(Collectors.toList());
        
        NodeRegistryResponse response = new NodeRegistryResponse();
        response.setJobKey(jobKey);
        response.setNodes(nodeDetails);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取所有活跃节点
     */
    @GetMapping("/registry/nodes/active")
    public ResponseEntity<List<NodeInfo>> getActiveNodes() {
        List<NodeInfo> activeNodes = nodeRegistryService.getRegistryStore().getActiveNodes();
        return ResponseEntity.ok(activeNodes);
    }
    
    /**
     * 重新调度作业
     */
    @PostMapping("/jobs/{jobKey}/reschedule")
    public ResponseEntity<Map<String, Object>> rescheduleJob(@PathVariable String jobKey) {
        boolean success = jobRegistry.triggerJob(jobKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("jobKey", jobKey);
        response.put("message", success ? "Job rescheduled successfully" : "Failed to reschedule job");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 暂停作业
     */
    @PostMapping("/jobs/{jobKey}/pause")
    public ResponseEntity<Map<String, Object>> pauseJob(@PathVariable String jobKey) {
        boolean success = jobRegistry.pauseJob(jobKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("jobKey", jobKey);
        response.put("message", success ? "Job paused successfully" : "Failed to pause job");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 恢复作业
     */
    @PostMapping("/jobs/{jobKey}/resume")
    public ResponseEntity<Map<String, Object>> resumeJob(@PathVariable String jobKey) {
        boolean success = jobRegistry.resumeJob(jobKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("jobKey", jobKey);
        response.put("message", success ? "Job resumed successfully" : "Failed to resume job");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取所有作业
     */
    @GetMapping("/jobs")
    public ResponseEntity<List<JobInfo>> getAllJobs() {
        List<JobInfo> jobs = jobRegistry.getAllJobs();
        return ResponseEntity.ok(jobs);
    }
    
    /**
     * 获取作业详情
     */
    @GetMapping("/jobs/{jobKey}")
    public ResponseEntity<JobInfo> getJobInfo(@PathVariable String jobKey) {
        JobInfo jobInfo = jobRegistry.getJobInfo(jobKey);
        if (jobInfo != null) {
            return ResponseEntity.ok(jobInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Data
    public static class NodeRegistryResponse {
        private String jobKey;
        private List<NodeDetail> nodes;
        
        @Data
        public static class NodeDetail {
            private String nodeId;
            private int[] shards;
            private Map<String, Object> metadata;
            private String status;
            private long lastHeartbeat;
        }
    }
} 