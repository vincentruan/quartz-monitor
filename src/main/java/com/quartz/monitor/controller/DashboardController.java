package com.quartz.monitor.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quartz.monitor.core.JobContainer;
import com.quartz.monitor.core.QuartzInstanceContainer;
import com.quartz.monitor.core.TriggerContainer;
import com.quartz.monitor.object.Job;
import com.quartz.monitor.conf.QuartzConfig;
import com.quartz.monitor.object.QuartzInstance;
import com.quartz.monitor.object.Scheduler;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    
    /**
     * 获取系统统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        
        // 实例数量
        List<QuartzConfig> configs = QuartzInstanceContainer.getAllQuartzConfigs();
        data.put("instanceCount", configs != null ? configs.size() : 0);
        
        // 调度器数量
        int schedulerCount = 0;
        List<QuartzInstance> instances = QuartzInstanceContainer.getAllQuartzInstance();
        for (QuartzInstance instance : instances) {
            List<Scheduler> schedulers = instance.getSchedulerList();
            schedulerCount += (schedulers != null ? schedulers.size() : 0);
        }
        data.put("schedulerCount", schedulerCount);
        
        // 任务数量
        Map<String, Job> jobMap = JobContainer.getJobMap();
        data.put("jobCount", jobMap != null ? jobMap.size() : 0);
        
        // 触发器数量
        data.put("triggerCount", TriggerContainer.getTriggerCount());
        
        // 运行中的任务数量
        int runningJobs = 0;
        for (Job job : jobMap.values()) {
            if ("NORMAL".equalsIgnoreCase(job.getState())) {
                runningJobs++;
            }
        }
        data.put("runningJobCount", runningJobs);
        
        response.put("code", 200);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取最近执行的任务
     */
    @GetMapping("/recent-jobs")
    public ResponseEntity<Map<String, Object>> getRecentJobs() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, Job> jobMap = JobContainer.getJobMap();
        List<Job> recentJobs = new ArrayList<>();
        
        if (jobMap != null && !jobMap.isEmpty()) {
            jobMap.values().stream()
                .sorted((j1, j2) -> {
                    if (j1.getNextFireTime() == null && j2.getNextFireTime() == null) {
                        return 0;
                    } else if (j1.getNextFireTime() == null) {
                        return 1;
                    } else if (j2.getNextFireTime() == null) {
                        return -1;
                    } else {
                        return j1.getNextFireTime().compareTo(j2.getNextFireTime());
                    }
                })
                .limit(10)
                .forEach(recentJobs::add);
        }
        
        response.put("code", 200);
        response.put("data", recentJobs);
        return ResponseEntity.ok(response);
    }
} 