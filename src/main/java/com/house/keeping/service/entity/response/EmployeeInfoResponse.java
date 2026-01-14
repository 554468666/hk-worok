package com.house.keeping.service.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "员工信息响应")
public class EmployeeInfoResponse {
    @Schema(description = "员工ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "角色：employee/manager/admin")
    private String role;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "状态：active/disabled")
    private String status;

    @Schema(description = "是否实名认证")
    private Boolean isVerified;

    @Schema(description = "身份证号（脱敏）")
    private String idCard;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "加入日期")
    private String joinDate;

    @Schema(description = "工龄")
    private String workYears;

    @Schema(description = "身份")
    private String identity;

    @Schema(description = "是否团队负责人")
    private Boolean isTeamLeader;

    @Schema(description = "团队人数")
    private String teamSize;

    @Schema(description = "履历")
    private String resume;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "更新时间")
    private Date updatedAt;
}
