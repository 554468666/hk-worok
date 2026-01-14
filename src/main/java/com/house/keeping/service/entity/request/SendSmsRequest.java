package com.house.keeping.service.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 发送短信验证码请求
 */
@Data
@Schema(description = "发送短信验证码请求")
public class SendSmsRequest {
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138001", required = true)
    private String phone;
    
    @NotBlank(message = "验证码类型不能为空")
    @Schema(description = "验证码类型：login(登录)/register(注册)/reset(密码重置)/bind(绑定)", 
            example = "login", required = true)
    private String type;
}
