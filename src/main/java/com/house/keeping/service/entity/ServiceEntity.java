package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("service")
public class ServiceEntity {
    @TableId
    private Long id;
    private String name;
    private Double price;
    private Boolean isHot;
    private java.util.Date createdAt;
}