package com.quartz.monitor.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 响应工具类
 */
public class ResponseUtils {
    
    /**
     * 成功响应
     */
    public static ResponseEntity<Map<String, Object>> success() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "操作成功");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 成功响应，带数据
     */
    public static ResponseEntity<Map<String, Object>> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "操作成功");
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 成功响应，带消息和数据
     */
    public static ResponseEntity<Map<String, Object>> success(String message, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", message);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 失败响应
     */
    public static ResponseEntity<Map<String, Object>> error(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
    
    /**
     * 失败响应，带状态码
     */
    public static ResponseEntity<Map<String, Object>> error(int code, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        return ResponseEntity.status(HttpStatus.valueOf(code)).body(result);
    }
    
    /**
     * 参数错误响应
     */
    public static ResponseEntity<Map<String, Object>> badRequest(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        result.put("message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
    
    /**
     * 未授权响应
     */
    public static ResponseEntity<Map<String, Object>> unauthorized(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    
    /**
     * 禁止访问响应
     */
    public static ResponseEntity<Map<String, Object>> forbidden(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 403);
        result.put("message", message);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
    }
    
    /**
     * 资源未找到响应
     */
    public static ResponseEntity<Map<String, Object>> notFound(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 404);
        result.put("message", message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }
} 