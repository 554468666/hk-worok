package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("user")
public class UserEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("`name`")  // 字段名也是MySQL关键字
    private String name;

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
     * 是否是会员(0-否 1-是)
     */
    @TableField("is_member")
    private Boolean isMember;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private Date createdAt;
}