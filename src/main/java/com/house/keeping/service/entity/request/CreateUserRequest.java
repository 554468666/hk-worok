package com.house.keeping.service.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建用户请求
 */
@Data
@Schema(description = "创建用户请求")
public class CreateUserRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20位之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @Schema(description = "用户名", example = "newuser", required = true)
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    @Schema(description = "密码", example = "123456", required = true)
    private String password;
    
    @Size(max = 20, message = "昵称不能超过20个字符")
    @Schema(description = "昵称", example = "新用户")
    private String nickname;
    
    @Schema(description = "角色：member/manager/admin", example = "member")
    private String role;
    
    @Schema(description = "邮箱", example = "newuser@example.com")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138002")
    private String phone;
    
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    @Schema(description = "身份证号", example = "110101199001011234")
    private String idCard;
    
    @Size(max = 200, message = "地址不能超过200个字符")
    @Schema(description = "地址", example = "北京市朝阳区建国路88号")
    private String address;
}
