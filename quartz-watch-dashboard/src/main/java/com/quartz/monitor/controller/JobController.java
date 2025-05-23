package com.quartz.monitor.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quartz.monitor.core.JobContainer;
import com.quartz.monitor.core.QuartzInstanceContainer;
import com.quartz.monitor.vo.Job;
import com.quartz.monitor.vo.QuartzInstance;
import com.quartz.monitor.vo.Scheduler;

@RestController
@RequestMapping("/api/job")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);
    
    /**
     * 获取任务列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "numPerPage", defaultValue = "20") Integer numPerPage) {
        
        Map<String, Object> response = new HashMap<>();
        List<Job> jobList = new ArrayList<>();
        
        try {
            QuartzInstance instance = QuartzInstanceContainer.getAllQuartzInstance().isEmpty() ? 
                null : QuartzInstanceContainer.getAllQuartzInstance().get(0);
            if (instance == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("code", 400, "message", "请先配置Quartz"));
            }
            
            List<Scheduler> schedulers = instance.getSchedulerList();
            log.info("schedulers list size:" + schedulers.size());
            
            if (schedulers != null && schedulers.size() > 0) {
                for (int i = 0; i < schedulers.size(); i++) {
                    Scheduler scheduler = schedulers.get(i);
                    Set<Job> jobSet = instance.getJmxAdapter().queryAllJobs(instance, scheduler);
                    for (Job job : jobSet) {
                        String id = UUID.randomUUID().toString();
                        job.setUuid(id);
                        JobContainer.addJob(id, job);
                        jobList.add(job);
                    }
                }
            }
            
            int total = jobList.size();
            int pageCount = (total + numPerPage - 1) / numPerPage; // 计算总页数
            if (pageNum < 1) {
                pageNum = 1;
            }
            if (pageNum > pageCount) {
                pageNum = pageCount;
            }
            
            // 计算分页 (如果需要)
            int fromIndex = (pageNum - 1) * numPerPage;
            int toIndex = Math.min(fromIndex + numPerPage, total);
            
            List<Job> pageData = fromIndex < total ? jobList.subList(fromIndex, toIndex) : new ArrayList<>();
            
            response.put("code", 200);
            response.put("data", pageData);
            response.put("total", total);
            response.put("pageNum", pageNum);
            response.put("pageCount", pageCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取任务列表失败", e);
            response.put("code", 500);
            response.put("message", "获取任务列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 执行任务
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> start(@RequestParam("uuid") String uuid) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            QuartzInstance instance = QuartzInstanceContainer.getAllQuartzInstance().isEmpty() ? 
                null : QuartzInstanceContainer.getAllQuartzInstance().get(0);
            Job job = JobContainer.getJobById(uuid);
            
            if (job == null) {
                response.put("code", 400);
                response.put("message", "任务不存在");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            instance.getJmxAdapter().startJobNow(instance,
                    instance.getSchedulerByName(job.getSchedulerName()), job);
            
            response.put("code", 200);
            response.put("message", "执行成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("执行任务失败", e);
            response.put("code", 500);
            response.put("message", "执行任务失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 删除任务
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestParam("uuid") String uuid) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            QuartzInstance instance = QuartzInstanceContainer.getAllQuartzInstance().isEmpty() ? 
                null : QuartzInstanceContainer.getAllQuartzInstance().get(0);
            Job job = JobContainer.getJobById(uuid);
            
            if (job == null) {
                response.put("code", 400);
                response.put("message", "任务不存在");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            JobContainer.removeJobById(uuid);
            log.info("delete a quartz job!");
            instance.getJmxAdapter().deleteJob(instance,
                    instance.getSchedulerByName(job.getSchedulerName()), job);
            
            response.put("code", 200);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("删除任务失败", e);
            response.put("code", 500);
            response.put("message", "删除任务失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 暂停任务
     */
    @PostMapping("/pause")
    public ResponseEntity<Map<String, Object>> pause(@RequestParam("uuid") String uuid) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            QuartzInstance instance = QuartzInstanceContainer.getAllQuartzInstance().isEmpty() ? 
                null : QuartzInstanceContainer.getAllQuartzInstance().get(0);
            Job job = JobContainer.getJobById(uuid);
            
            if (job == null) {
                response.put("code", 400);
                response.put("message", "任务不存在");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            log.info("pause a quartz job!");
            instance.getJmxAdapter().pauseJob(instance,
                    instance.getSchedulerByName(job.getSchedulerName()), job);
            
            response.put("code", 200);
            response.put("message", "任务已暂停");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("暂停任务失败", e);
            response.put("code", 500);
            response.put("message", "暂停任务失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 恢复任务
     */
    @PostMapping("/resume")
    public ResponseEntity<Map<String, Object>> resume(@RequestParam("uuid") String uuid) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            QuartzInstance instance = QuartzInstanceContainer.getAllQuartzInstance().isEmpty() ? 
                null : QuartzInstanceContainer.getAllQuartzInstance().get(0);
            Job job = JobContainer.getJobById(uuid);
            
            if (job == null) {
                response.put("code", 400);
                response.put("message", "任务不存在");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            log.info("resume a quartz job!");
            instance.getJmxAdapter().resumeJob(instance,
                    instance.getSchedulerByName(job.getSchedulerName()), job);
            
            response.put("code", 200);
            response.put("message", "任务已恢复");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("恢复任务失败", e);
            response.put("code", 500);
            response.put("message", "恢复任务失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取可用的调度器列表
     */
    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> getJobTypes() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Job> jobMap = JobContainer.getJobMap();
            Set<String> schedulerNames = new HashSet<>();
            
            for (Map.Entry<String, Job> entry : jobMap.entrySet()) {
                schedulerNames.add(entry.getValue().getSchedulerName());
            }
            
            response.put("code", 200);
            response.put("data", Map.of(
                "schedulers", schedulerNames,
                "jobs", jobMap
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取调度器列表失败", e);
            response.put("code", 500);
            response.put("message", "获取调度器列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 添加任务
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> add(@RequestBody Job job) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            QuartzInstance instance = QuartzInstanceContainer.getAllQuartzInstance().isEmpty() ? 
                null : QuartzInstanceContainer.getAllQuartzInstance().get(0);
            
            if (job == null) {
                response.put("code", 400);
                response.put("message", "任务参数不能为空");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            Map<String, Object> map = new HashMap<>();
            map.put("name", job.getJobName());
            map.put("group", job.getGroup());
            map.put("description", job.getDescription());
            map.put("jobClass", JobContainer.getJobById(job.getJobClass()).getJobClass());
            map.put("durability", true);
            map.put("jobDetailClass", "org.quartz.impl.JobDetailImpl");
            
            instance.getJmxAdapter().addJob(instance,
                    instance.getSchedulerByName(job.getSchedulerName()), map);
            log.info("add job successfully!");
            
            response.put("code", 200);
            response.put("message", "添加成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("添加任务失败", e);
            response.put("code", 500);
            response.put("message", "添加任务失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 