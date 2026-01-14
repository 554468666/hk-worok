package com.house.keeping.service.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<?>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        
        Result<?> result = Result.error(e.getCode(), e.getMessage(), e.getError());
        
        // 将错误码映射到HTTP状态码
        HttpStatus httpStatus = mapErrorCodeToHttpStatus(e.getCode());
        return ResponseEntity.status(httpStatus).body(result);
    }
    
    /**
     * 参数验证异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, Object> details = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            details.put(error.getField(), error.getDefaultMessage());
        }
        
        log.warn("参数验证失败: {}", details);
        
        Result<?> result = Result.error(
            ErrorCode.BAD_REQUEST.getCode(),
            "请求参数验证失败",
            "Validation failed",
            details
        );
        
        return ResponseEntity.badRequest().body(result);
    }
    
    /**
     * 绑定异常处理
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<?>> handleBindException(BindException e) {
        Map<String, Object> details = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            details.put(error.getField(), error.getDefaultMessage());
        }
        
        log.warn("绑定异常: {}", details);
        
        Result<?> result = Result.error(
            ErrorCode.BAD_REQUEST.getCode(),
            "请求参数绑定失败",
            "Bind failed",
            details
        );
        
        return ResponseEntity.badRequest().body(result);
    }
    
    /**
     * 非法参数异常处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数异常: {}", e.getMessage());
        
        Result<?> result = Result.error(
            ErrorCode.BAD_REQUEST.getCode(),
            "请求参数错误",
            e.getMessage()
        );
        
        return ResponseEntity.badRequest().body(result);
    }
    
    /**
     * 通用异常处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e) {
        log.error("系统异常", e);
        
        Result<?> result = Result.error(
            ErrorCode.INTERNAL_ERROR.getCode(),
            "服务器内部错误",
            e.getMessage()
        );
        
        return ResponseEntity.internalServerError().body(result);
    }
    
    /**
     * 将错误码映射到HTTP状态码
     */
    private HttpStatus mapErrorCodeToHttpStatus(Integer code) {
        if (code == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        // 业务错误码映射
        if (code >= 10000 && code < 20000) {
            // 认证相关错误
            if (code == 10001 || code == 10004 || code == 10005) {
                return HttpStatus.UNAUTHORIZED;
            }
        }
        
        if (code == 40001) {
            return HttpStatus.FORBIDDEN;
        }
        
        if (code == 409) {
            return HttpStatus.CONFLICT;
        }
        
        if (code == 429 || code == 10011) {
            return HttpStatus.TOO_MANY_REQUESTS;
        }
        
        if (code == 50001) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        
        return HttpStatus.BAD_REQUEST;
    }
}
