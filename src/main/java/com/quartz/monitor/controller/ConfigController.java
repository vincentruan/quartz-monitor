package com.quartz.monitor.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.uuid.Generators;
import com.quartz.monitor.conf.QuartzConfig;
import com.quartz.monitor.core.QuartzConnectService;
import com.quartz.monitor.core.QuartzConnectServiceImpl;
import com.quartz.monitor.core.QuartzInstanceContainer;
import com.quartz.monitor.db.QuartzConfigService;
import com.quartz.monitor.object.QuartzInstance;
import com.quartz.monitor.util.ResponseUtils;

@RestController
@RequestMapping("/api/config")
public class ConfigController {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private QuartzConfigService quartzConfigService;
    
    /**
     * 获取Quartz实例列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list() {
        try {
            List<QuartzConfig> configs = quartzConfigService.queryAllQuartzConfigs();
            return ResponseUtils.success(configs);
        } catch (Exception e) {
            log.error("获取Quartz实例列表失败", e);
            return ResponseUtils.error("获取Quartz实例列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取Quartz实例详情
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable("uuid") String uuid) {
        if (StringUtils.isBlank(uuid)) {
            return ResponseUtils.badRequest("实例ID不能为空");
        }
        
        try {
            QuartzConfig config = quartzConfigService.getQuartzConfigByUuid(uuid);
            
            if (config == null) {
                return ResponseUtils.notFound("实例不存在");
            }
            
            return ResponseUtils.success(config);
        } catch (Exception e) {
            log.error("获取Quartz实例详情失败", e);
            return ResponseUtils.error("获取Quartz实例详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加Quartz实例配置
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> add(@RequestBody QuartzConfig config) {
        if (config == null || !config.isValid()) {
            return ResponseUtils.badRequest("参数不正确");
        }
        
        try {
            UUID uuid = Generators.timeBasedEpochRandomGenerator().generate();
            config.setUuid(uuid.toString());
            
            boolean saveResult = quartzConfigService.addQuartzConfig(config);
            
            if (saveResult) {
                QuartzInstanceContainer.addQuartzConfig(config);
                
                QuartzConnectService quartzConnectService = new QuartzConnectServiceImpl();
                try {
                    QuartzInstance quartzInstance = quartzConnectService.initInstance(config);
                    QuartzInstanceContainer.addQuartzInstance(config.getUuid(), quartzInstance);
                } catch (Exception e) {
                    log.error("无法初始化Quartz实例", e);
                }
                
                return ResponseUtils.success("添加成功", config);
            } else {
                return ResponseUtils.error("添加失败");
            }
        } catch (Exception e) {
            log.error("添加Quartz实例失败", e);
            return ResponseUtils.error("添加Quartz实例失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新Quartz实例配置
     */
    @PutMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable("uuid") String uuid, @RequestBody QuartzConfig config) {
        if (config == null || StringUtils.isBlank(uuid)) {
            return ResponseUtils.badRequest("参数不正确");
        }
        
        try {
            config.setUuid(uuid);
            
            // 关闭旧的连接
            QuartzInstance oldInstance = QuartzInstanceContainer.getQuartzInstanceById(uuid);
            if (oldInstance != null) {
                QuartzConnectService service = new QuartzConnectServiceImpl();
                service.shutdown(oldInstance);
            }
            
            boolean updateResult = quartzConfigService.updateQuartzConfig(config);
            
            if (updateResult) {
                QuartzInstanceContainer.removeQuartzConfig(uuid);
                QuartzInstanceContainer.removeQuartzInstance(uuid);
                
                QuartzInstanceContainer.addQuartzConfig(config);
                
                QuartzConnectService quartzConnectService = new QuartzConnectServiceImpl();
                try {
                    QuartzInstance quartzInstance = quartzConnectService.initInstance(config);
                    QuartzInstanceContainer.addQuartzInstance(config.getUuid(), quartzInstance);
                } catch (Exception e) {
                    log.error("无法初始化Quartz实例", e);
                }
                
                return ResponseUtils.success("更新成功", config);
            } else {
                return ResponseUtils.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新Quartz实例失败", e);
            return ResponseUtils.error("更新Quartz实例失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除Quartz实例配置
     */
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("uuid") String uuid) {
        if (StringUtils.isBlank(uuid)) {
            return ResponseUtils.badRequest("实例ID不能为空");
        }
        
        try {
            // 关闭连接
            QuartzInstance instance = QuartzInstanceContainer.getQuartzInstanceById(uuid);
            if (instance != null) {
                QuartzConnectService service = new QuartzConnectServiceImpl();
                service.shutdown(instance);
            }
            
            boolean deleteResult = quartzConfigService.deleteQuartzConfig(uuid);
            
            if (deleteResult) {
                QuartzInstanceContainer.removeQuartzConfig(uuid);
                QuartzInstanceContainer.removeQuartzInstance(uuid);
                
                return ResponseUtils.success("删除成功");
            } else {
                return ResponseUtils.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除Quartz实例失败", e);
            return ResponseUtils.error("删除Quartz实例失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试Quartz实例连接
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection(@RequestBody QuartzConfig config) {
        if (config == null || !config.isValid()) {
            return ResponseUtils.badRequest("参数不正确");
        }
        
        try {
            QuartzConnectService quartzConnectService = new QuartzConnectServiceImpl();
            QuartzInstance quartzInstance = quartzConnectService.initInstance(config);
            
            if (quartzInstance != null && quartzInstance.getSchedulerList() != null && !quartzInstance.getSchedulerList().isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put("schedulers", quartzInstance.getSchedulerList().size());
                data.put("message", "连接成功");
                
                // 关闭测试连接
                quartzConnectService.shutdown(quartzInstance);
                
                return ResponseUtils.success(data);
            } else {
                return ResponseUtils.error("连接失败: 没有找到调度器");
            }
        } catch (Exception e) {
            log.error("测试连接失败", e);
            return ResponseUtils.error("连接失败: " + e.getMessage());
        }
    }
} 