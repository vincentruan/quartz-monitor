package com.quartz.monitor.service;

import com.quartz.monitor.dto.PageResult;
import com.quartz.monitor.dto.TriggerInfo;
import com.quartz.monitor.dto.request.CreateTriggerRequest;
import com.quartz.monitor.dto.request.UpdateTriggerRequest;
import com.quartz.monitor.dto.response.TriggerDetailResponse;

/**
 * Trigger服务接口
 */
public interface TriggerService {
    
    /**
     * 获取指定Job的触发器列表
     * @param schedulerName 调度器名称
     * @param jobGroup Job组
     * @param jobName Job名称
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<TriggerInfo> getTriggersForJob(String schedulerName, String jobGroup, 
                                            String jobName, int page, int size);
    
    /**
     * 获取触发器详情
     * @param schedulerName 调度器名称
     * @param triggerGroup 触发器组
     * @param triggerName 触发器名称
     * @return 触发器详情
     */
    TriggerDetailResponse getTriggerDetail(String schedulerName, String triggerGroup, 
                                         String triggerName);
    
    /**
     * 创建新触发器
     * @param request 创建请求
     * @return 创建的触发器信息
     */
    TriggerInfo createTrigger(CreateTriggerRequest request);
    
    /**
     * 更新触发器
     * @param schedulerName 调度器名称
     * @param triggerGroup 触发器组
     * @param triggerName 触发器名称
     * @param request 更新请求
     * @return 更新后的触发器信息
     */
    TriggerInfo updateTrigger(String schedulerName, String triggerGroup, 
                            String triggerName, UpdateTriggerRequest request);
    
    /**
     * 删除触发器
     * @param schedulerName 调度器名称
     * @param triggerGroup 触发器组
     * @param triggerName 触发器名称
     */
    void deleteTrigger(String schedulerName, String triggerGroup, String triggerName);
    
    /**
     * 暂停触发器
     * @param schedulerName 调度器名称
     * @param triggerGroup 触发器组
     * @param triggerName 触发器名称
     */
    void pauseTrigger(String schedulerName, String triggerGroup, String triggerName);
    
    /**
     * 恢复触发器
     * @param schedulerName 调度器名称
     * @param triggerGroup 触发器组
     * @param triggerName 触发器名称
     */
    void resumeTrigger(String schedulerName, String triggerGroup, String triggerName);
} 