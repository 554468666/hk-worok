package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_config")
public class SysConfigEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("`name`")
    private String name;

    @TableField("config_key")
    private String configKey;

    @TableField("config_value")
    private String configValue;
}
