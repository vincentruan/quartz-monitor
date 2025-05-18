package com.quartz.monitor.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.quartz.monitor.core.TriggerContainer;
import com.quartz.monitor.object.Job;
import com.quartz.monitor.object.QuartzInstance;
import com.quartz.monitor.object.Scheduler;
import com.quartz.monitor.object.Trigger;
import com.quartz.monitor.object.TriggerInput;
import com.quartz.monitor.util.Tools;

@RestController
@RequestMapping("/api/trigger")
public class TriggerController {

    private static final Logger log = LoggerFactory.getLogger(TriggerController.class);
    
    /**
     * 获取触发器列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestParam("jobId") String jobId) {
        Map<String, Object> response = new HashMap<>();
        List<Trigger> triggerList = new ArrayList<>();
        
        try {
            QuartzInstance instance = Tools.getQuartzInstance();
            
            Job job = JobContainer.getJobById(jobId);
            if (job == null) {
                response.put("code", 400);
                response.put("message", "任务不存在");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            Scheduler scheduler = instance.getSchedulerByName(job.getSchedulerName());
            
            List<Trigger> temp = instance.getJmxAdapter().getTriggersForJob(instance, scheduler, job.getJobName(), job.getGroup());
            if (temp != null && temp.size() > 0) {
                for (Trigger trigger : temp) {
                    String id = Tools.generateUUID();
                    trigger.setUuid(id);
                    trigger.setJobId(jobId);
                    TriggerContainer.addTrigger(id, trigger);
                    triggerList.add(trigger);
                }
            }
            
            log.info("job[" + job.getJobName() + "]'s trigger size:" + triggerList.size());
            
            response.put("code", 200);
            response.put("data", triggerList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取触发器列表失败", e);
            response.put("code", 500);
            response.put("message", "获取触发器列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 添加触发器
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> add(@RequestBody TriggerInput triggerInput) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            QuartzInstance instance = Tools.getQuartzInstance();
            
            if (triggerInput == null) {
                response.put("code", 400);
                response.put("message", "触发器参数不能为空");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            Job job = JobContainer.getJobById(triggerInput.getJobId());
            if (job == null) {
                response.put("code", 400);
                response.put("message", "任务不存在");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            HashMap<String, Object> triggerMap = new HashMap<>();
            triggerMap.put("name", triggerInput.getName());
            triggerMap.put("group", triggerInput.getGroup());
            triggerMap.put("description", triggerInput.getDescription());
            
            if (triggerInput.getDateFlag() == 1) {
                triggerMap.put("startTime", triggerInput.getDate());
                triggerMap.put("triggerClass", "org.quartz.impl.triggers.SimpleTriggerImpl");
            } else {
                triggerMap.put("cronExpression", triggerInput.getCron());
                triggerMap.put("triggerClass", "org.quartz.impl.triggers.CronTriggerImpl");
            }
            
            triggerMap.put("jobName", job.getJobName());
            triggerMap.put("jobGroup", job.getGroup());
            
            instance.getJmxAdapter().addTriggerForJob(instance, 
                    instance.getSchedulerByName(job.getSchedulerName()), job, triggerMap);
            
            log.info("add trigger for job:" + job.getJobName());
            
            response.put("code", 200);
            response.put("message", "添加成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("添加触发器失败", e);
            response.put("code", 500);
            response.put("message", "添加触发器失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 删除触发器
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestParam("uuid") String uuid) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            QuartzInstance instance = Tools.getQuartzInstance();
            
            Trigger trigger = TriggerContainer.getTriggerById(uuid);
            if (trigger == null) {
                response.put("code", 400);
                response.put("message", "触发器不存在");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            TriggerContainer.removeTriggerById(uuid);
            
            Job job = JobContainer.getJobById(trigger.getJobId());
            instance.getJmxAdapter().deleteTrigger(instance, 
                    instance.getSchedulerByName(job.getSchedulerName()), trigger);
            
            log.info("delete job[" + trigger.getJobName() + "]'s trigger!");
            
            response.put("code", 200);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("删除触发器失败", e);
            response.put("code", 500);
            response.put("message", "删除触发器失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 