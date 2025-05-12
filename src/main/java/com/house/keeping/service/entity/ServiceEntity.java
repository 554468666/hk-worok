package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("service")
public class ServiceEntity {
    @TableId
    @Schema(description = "服务产品编号")
    private Long id;

    @Schema(description = "服务产品名称")
    private String name;

    @Schema(description = "服务产品金额")
    private Double price;

    @Schema(description = "服务产品是否热门")
    private Boolean isHot;

    @Schema(description = "服务产品上架时间")
    private java.util.Date createdAt;

    @Schema(description = "服务产品描述")
    private String describes;

    @Schema(description = "服务产品图片路径")
    private String imagePath;
}