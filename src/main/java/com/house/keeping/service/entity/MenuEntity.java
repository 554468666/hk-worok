package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("menu_info")
public class MenuEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("menu_name")
    private String menuName;

    @TableField("menu_type")
    private String menuType;

    @TableField("menu_status")
    private Boolean menuStatus;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(value = "create_user", fill = FieldFill.INSERT)
    private String createUser;

    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    private String updateUser;
}
