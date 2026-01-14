package com.house.keeping.service.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 登录响应
 */
@Data
@Schema(description = "登录响应")
public class LoginResponse {
    
    @Schema(description = "访问令牌")
    private String token;
    
    @Schema(description = "令牌类型")
    private String tokenType;
    
    @Schema(description = "过期时间（秒）")
    private Long expiresIn;
    
    @Schema(description = "刷新令牌")
    private String refreshToken;
    
    @Schema(description = "用户信息")
    private UserInfoResponse userInfo;
    
    @Schema(description = "是否新用户")
    private Boolean isNewUser;
}
