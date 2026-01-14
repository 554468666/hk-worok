package com.house.keeping.service.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实名认证请求
 */
@Data
@Schema(description = "实名认证请求")
public class VerifyIdentityRequest {
    
    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 10, message = "姓名长度必须在2-10个汉字之间")
    @Pattern(regexp = "^[\u4e00-\u9fa5]+$", message = "姓名只能包含汉字")
    @Schema(description = "真实姓名", example = "张三", required = true)
    private String realName;
    
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    @Schema(description = "身份证号", example = "110101199001011234", required = true)
    private String idCard;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138001", required = true)
    private String phone;
}
