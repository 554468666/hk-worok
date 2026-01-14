package com.house.keeping.service.common;

import lombok.Data;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 统一响应格式
 */
@Data
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private String timestamp;
    
    /**
     * 追踪ID
     */
    private String traceId;
    
    /**
     * 错误详情（仅错误时返回）
     */
    private String error;
    
    /**
     * 额外详情
     */
    private Map<String, Object> details;
    
    public Result() {
        this.timestamp = Instant.now().toString();
        this.traceId = UUID.randomUUID().toString();
    }
    
    public Result(Integer code, String message, T data) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }
    
    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    /**
     * 成功响应（自定义消息）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    /**
     * 失败响应
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>(code, message, null);
        return result;
    }
    
    /**
     * 失败响应（默认500）
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
    
    /**
     * 业务错误响应
     */
    public static <T> Result<T> error(Integer code, String message, String error) {
        Result<T> result = new Result<>(code, message, null);
        result.setError(error);
        return result;
    }
    
    /**
     * 业务错误响应（带详情）
     */
    public static <T> Result<T> error(Integer code, String message, String error, Map<String, Object> details) {
        Result<T> result = new Result<>(code, message, null);
        result.setError(error);
        result.setDetails(details);
        return result;
    }
    
    /**
     * 添加详情
     */
    public Result<T> addDetail(String key, Object value) {
        if (this.details == null) {
            this.details = new HashMap<>();
        }
        this.details.put(key, value);
        return this;
    }
    
    /**
     * 设置错误信息
     */
    public Result<T> setErrorInfo(String error) {
        this.error = error;
        return this;
    }
    
    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}
