package com.quartz.monitor.dto.response;

/**
 * 统一API响应格式
 */
public class ApiResponse<T> {
    private int code;
    private String desc;
    private T data;

    public ApiResponse(int code, String desc, T data) {
        this.code = code;
        this.desc = desc;
        this.data = data;
    }

    public ApiResponse(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static ApiResponse<?> error(int code, String desc) {
        return new ApiResponse<>(code, desc);
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
