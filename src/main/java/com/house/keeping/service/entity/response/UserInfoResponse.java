package com.house.keeping.service.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户信息响应
 */
@Data
@Schema(description = "用户信息响应")
public class UserInfoResponse {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "角色：admin/manager/member")
    private String role;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "状态：active/disabled")
    private String status;
    
    @Schema(description = "是否实名认证")
    private Boolean isVerified;
    
    @Schema(description = "身份证号（脱敏）")
    private String idCard;
    
    @Schema(description = "地址")
    private String address;
    
    @Schema(description = "微信OpenID")
    private String wechatOpenId;
    
    @Schema(description = "微信UnionID")
    private String wechatUnionId;
    
    @Schema(description = "加入日期")
    private String joinDate;
    
    @Schema(description = "最后登录时间")
    private String lastLogin;
    
    @Schema(description = "权限列表")
    private List<String> permissions;
    
    @Schema(description = "登录次数")
    private Integer loginCount;
    
    @Schema(description = "创建时间")
    private Date createdAt;
    
    @Schema(description = "更新时间")
    private Date updatedAt;
}
