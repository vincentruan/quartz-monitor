package com.quartz.monitor.dto;

public class CronResponse<T> {
    private int code;
    private String message;
    private T data;

    public CronResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CronResponse<T> success(T data) {
        return new CronResponse<>(200, "Success", data);
    }

    public static <T> CronResponse<T> error(int code, String message) {
        return new CronResponse<>(code, message, null);
    }

    // Getters
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
