package com.quartz.monitor.exception;

import javax.validation.ConstraintViolationException;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.quartz.monitor.common.ErrorCode;
import com.quartz.monitor.dto.response.ErrorResponse;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        logger.error("Business exception: code={}, message={}", e.getCode(), e.getMessage());
        
        ErrorResponse response = new ErrorResponse(
            e.getCode(),
            e.getMessage(),
            e.getDetailMessage()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理调度器未找到异常
     */
    @ExceptionHandler(SchedulerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSchedulerNotFoundException(SchedulerNotFoundException e) {
        logger.error("Scheduler not found: {}", e.getMessage());
        
        ErrorResponse response = new ErrorResponse(
            ErrorCode.SCHEDULER_NOT_FOUND.getCode(),
            ErrorCode.SCHEDULER_NOT_FOUND.getMessage(),
            e.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * 处理任务未找到异常
     */
    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleJobNotFoundException(JobNotFoundException e) {
        logger.error("Job not found: {}", e.getMessage());
        
        ErrorResponse response = new ErrorResponse(
            ErrorCode.JOB_NOT_FOUND.getCode(),
            ErrorCode.JOB_NOT_FOUND.getMessage(),
            e.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * 处理触发器未找到异常
     */
    @ExceptionHandler(TriggerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTriggerNotFoundException(TriggerNotFoundException e) {
        logger.error("Trigger not found: {}", e.getMessage());
        
        ErrorResponse response = new ErrorResponse(
            ErrorCode.TRIGGER_NOT_FOUND.getCode(),
            ErrorCode.TRIGGER_NOT_FOUND.getMessage(),
            e.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * 处理调度器异常
     */
    @ExceptionHandler(SchedulerException.class)
    public ResponseEntity<ErrorResponse> handleSchedulerException(SchedulerException e) {
        logger.error("Scheduler exception", e);
        
        ErrorResponse response = new ErrorResponse(
            ErrorCode.SCHEDULER_ERROR.getCode(),
            ErrorCode.SCHEDULER_ERROR.getMessage(),
            null
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        logger.error("Validation error: {}", e.getMessage());
        
        FieldError fieldError = e.getBindingResult().getFieldError();
        String detail = fieldError != null ? 
            fieldError.getField() + " " + fieldError.getDefaultMessage() : 
            "Validation failed";
        
        ErrorResponse response = new ErrorResponse(
            ErrorCode.INVALID_PARAMETER.getCode(),
            ErrorCode.INVALID_PARAMETER.getMessage(),
            detail
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        logger.error("Constraint violation: {}", e.getMessage());
        
        ErrorResponse response = new ErrorResponse(
            ErrorCode.INVALID_PARAMETER.getCode(),
            ErrorCode.INVALID_PARAMETER.getMessage(),
            e.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        logger.error("Unexpected error", e);
        
        ErrorResponse response = new ErrorResponse(
            ErrorCode.INTERNAL_ERROR.getCode(),
            ErrorCode.INTERNAL_ERROR.getMessage(),
            null
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 