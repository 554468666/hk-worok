package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("`user`")
public class UserEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String name;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 微信开放平台ID
     */
    @TableField("open_id")
    private String openId;

    /**
     * 微信会话密钥
     */
    @TableField("session_key")
    private String sessionKey;

    /**
     * 微信UnionID
     */
    @TableField("union_id")
    private String unionId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 角色：admin/manager/member
     */
    @TableField("user_role")
    private String role;

    /**
     * 状态：active/disabled
     */
    private String status;

    /**
     * 是否实名认证
     */
    @TableField("is_verified")
    private Boolean isVerified;

    /**
     * 身份证号
     */
    @TableField("id_card")
    private String idCard;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 地址
     */
    private String address;

    /**
     * 头像图片路径
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 是否是管理员(0-否 1-是) - 保留字段，已废弃
     */
    @Deprecated
    @TableField("is_admin")
    private Boolean isAdmin;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private Date updatedAt;

    /**
     * 最后登录时间
     */
    @TableField("last_login")
    private Date lastLogin;

    /**
     * 登录次数
     */
    @TableField("login_count")
    private Integer loginCount;

    /**
     * 软删除标记
     */
    @TableField("is_deleted")
    private Boolean isDeleted;
}