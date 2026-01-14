package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Data
@TableName("hot_service")
public class HotServiceInfoEntity {

    @TableId
    @Schema(description = "主键Id")
    private Long id;

    @TableField("service_name")
    private String title;

    @TableField("service_id")
    private String serviceId;

    @TableField("service_remark")
    private String serviceRemark;

    @TableField("image_url")
    private String imageUrl;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "create_user", fill = FieldFill.INSERT)
    private String createUser;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    private String updateUser;
}
