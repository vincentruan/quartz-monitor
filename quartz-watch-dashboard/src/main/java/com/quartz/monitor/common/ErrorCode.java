package com.quartz.monitor.common;

/**
 * 错误码枚举
 */
public enum ErrorCode {
    
    // 通用错误码 1000-1999
    SUCCESS("0", "成功"),
    INTERNAL_ERROR("1000", "系统内部错误"),
    INVALID_PARAMETER("1001", "参数无效"),
    
    // Scheduler相关错误码 2000-2999
    SCHEDULER_NOT_FOUND("2000", "调度器不存在"),
    SCHEDULER_ERROR("2001", "调度器操作失败"),
    
    // Job相关错误码 3000-3999
    JOB_NOT_FOUND("3000", "任务不存在"),
    JOB_CLASS_NOT_FOUND("3001", "任务类不存在"),
    JOB_CLASS_INVALID("3002", "任务类无效"),
    JOB_CREATE_FAILED("3003", "创建任务失败"),
    JOB_UPDATE_FAILED("3004", "更新任务失败"),
    JOB_DELETE_FAILED("3005", "删除任务失败"),
    JOB_OPERATION_FAILED("3006", "任务操作失败"),
    
    // Trigger相关错误码 4000-4999
    TRIGGER_NOT_FOUND("4000", "触发器不存在"),
    TRIGGER_CREATE_FAILED("4001", "创建触发器失败"),
    TRIGGER_UPDATE_FAILED("4002", "更新触发器失败"),
    TRIGGER_DELETE_FAILED("4003", "删除触发器失败"),
    TRIGGER_OPERATION_FAILED("4004", "触发器操作失败"),
    TRIGGER_CRON_INVALID("4005", "Cron表达式无效");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
} 