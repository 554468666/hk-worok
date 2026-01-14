package com.house.keeping.service.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 手机号验证码登录请求
 */
@Data
@Schema(description = "手机号验证码登录请求")
public class PhoneLoginRequest {
    
    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号", example = "13800138001", required = true)
    private String phone;
    
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须是6位数字")
    @Schema(description = "验证码", example = "123456", required = true)
    private String code;
    
    @NotBlank(message = "会话ID不能为空")
    @Schema(description = "会话ID", example = "sms_session_20260105_123456", required = true)
    private String sessionId;
}
