package com.house.keeping.service.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新个人资料请求
 */
@Data
@Schema(description = "更新个人资料请求")
public class UpdateProfileRequest {
    
    @Size(max = 20, message = "昵称不能超过20个字符")
    @Schema(description = "昵称", example = "我的昵称")
    private String nickname;
    
    @Schema(description = "邮箱", example = "myemail@example.com")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138004")
    private String phone;
    
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    @Schema(description = "身份证号", example = "110101199001011234")
    private String idCard;
    
    @Size(max = 200, message = "地址不能超过200个字符")
    @Schema(description = "地址", example = "北京市朝阳区")
    private String address;
}
