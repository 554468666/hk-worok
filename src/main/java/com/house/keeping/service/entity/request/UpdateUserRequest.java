package com.house.keeping.service.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户请求
 */
@Data
@Schema(description = "更新用户请求")
public class UpdateUserRequest {
    
    @Size(max = 20, message = "昵称不能超过20个字符")
    @Schema(description = "昵称", example = "更新后的昵称")
    private String nickname;
    
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    @Schema(description = "新密码（不修改则不传）", example = "newpass123")
    private String password;
    
    @Schema(description = "角色：member/manager/admin", example = "member")
    private String role;
    
    @Schema(description = "邮箱", example = "updated@example.com")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138003")
    private String phone;
    
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    @Schema(description = "身份证号", example = "110101199001011234")
    private String idCard;
    
    @Size(max = 200, message = "地址不能超过200个字符")
    @Schema(description = "地址", example = "北京市海淀区中关村")
    private String address;
}
