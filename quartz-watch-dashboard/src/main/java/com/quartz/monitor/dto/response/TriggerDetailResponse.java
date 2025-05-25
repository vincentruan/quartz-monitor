package com.quartz.monitor.dto.response;

import com.quartz.monitor.dto.TriggerInfo;

/**
 * Response DTO for trigger details
 */
public class TriggerDetailResponse {
    
    private TriggerInfo triggerInfo;
    
    public TriggerDetailResponse() {
    }
    
    public TriggerDetailResponse(TriggerInfo triggerInfo) {
        this.triggerInfo = triggerInfo;
    }
    
    // Getters and setters
    public TriggerInfo getTriggerInfo() {
        return triggerInfo;
    }
    
    public void setTriggerInfo(TriggerInfo triggerInfo) {
        this.triggerInfo = triggerInfo;
    }
} 