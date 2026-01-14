package com.house.keeping.service.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改密码请求
 */
@Data
@Schema(description = "修改密码请求")
public class ChangePasswordRequest {
    
    @NotBlank(message = "当前密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    @Schema(description = "当前密码", example = "123456", required = true)
    private String currentPassword;
    
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    @Schema(description = "新密码", example = "newpass123", required = true)
    private String newPassword;
    
    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认新密码", example = "newpass123", required = true)
    private String confirmPassword;
}
