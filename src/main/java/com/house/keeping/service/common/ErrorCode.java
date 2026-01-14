package com.house.keeping.service.common;

/**
 * 错误码枚举
 */
public enum ErrorCode {
    
    // 通用错误
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权/登录失效"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    
    // 用户认证相关错误 10xxx
    USERNAME_OR_PASSWORD_ERROR(10001, "用户名或密码错误"),
    USER_NOT_EXIST(10002, "用户不存在"),
    USER_ALREADY_EXIST(10003, "用户已存在"),
    TOKEN_INVALID(10004, "Token无效"),
    TOKEN_EXPIRED(10005, "Token过期"),
    PASSWORD_ERROR_TOO_MANY(10006, "密码错误次数过多"),
    VERIFICATION_CODE_ERROR(10007, "验证码错误"),
    VERIFICATION_CODE_EXPIRED(10008, "验证码已过期"),
    PHONE_FORMAT_ERROR(10009, "手机号格式错误"),
    EMAIL_FORMAT_ERROR(10010, "邮箱格式错误"),
    SMS_SEND_TOO_FREQUENT(10011, "短信发送过于频繁"),
    
    // 任务管理相关错误 20xxx
    TASK_NOT_EXIST(20001, "任务不存在"),
    TASK_ALREADY_DELETED(20002, "任务已被删除"),
    TASK_STATUS_NOT_ALLOW(20003, "任务状态不允许该操作"),
    
    // 文件上传相关错误 30xxx
    FILE_UPLOAD_FAILED(30001, "文件上传失败"),
    FILE_TYPE_NOT_SUPPORTED(30002, "文件类型不支持"),
    FILE_SIZE_EXCEED(30003, "文件大小超限"),
    
    // 权限相关错误 40xxx
    PERMISSION_DENIED(40001, "权限不足"),
    
    // 系统相关错误 50xxx
    SYSTEM_MAINTENANCE(50001, "系统维护中");
    
    private final Integer code;
    private final String message;
    
    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    /**
     * 根据错误码获取枚举
     */
    public static ErrorCode fromCode(Integer code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return INTERNAL_ERROR;
    }
}
