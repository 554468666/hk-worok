package com.house.keeping.service.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录请求
 */
@Data
@Schema(description = "微信登录请求")
public class WeChatLoginRequest {
    
    @NotBlank(message = "微信授权码不能为空")
    @Schema(description = "微信授权码", example = "0612a3b6", required = true)
    private String code;
    
    @Schema(description = "微信用户信息")
    private WeChatUserInfo userInfo;
    
    @Data
    @Schema(description = "微信用户信息")
    public static class WeChatUserInfo {
        @Schema(description = "微信昵称", example = "微信用户")
        private String nickname;
        
        @Schema(description = "头像URL", example = "https://thirdwx.qlogo.cn/...")
        private String avatar;
        
        @Schema(description = "性别：1(男)/2(女)/0(未知)", example = "1")
        private Integer gender;
        
        @Schema(description = "城市", example = "深圳")
        private String city;
        
        @Schema(description = "省份", example = "广东")
        private String province;
        
        @Schema(description = "国家", example = "中国")
        private String country;
    }
}
