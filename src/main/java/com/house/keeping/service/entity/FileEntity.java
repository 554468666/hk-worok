package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_config")
public class FileEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("`key`")  // 字段名也是MySQL关键字
    private String key;

    @TableField("`name`")  // 字段名也是MySQL关键字
    private String name;

    @TableField("`path`")
    private String path;

    @TableField("`type`")
    private String type;

    @TableField(fill = FieldFill.INSERT)
    private Date create_time;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modify_time;

    @TableField(fill = FieldFill.INSERT)
    private String create_user;

    @TableField(fill = FieldFill.UPDATE)
    private String update_user;

}
