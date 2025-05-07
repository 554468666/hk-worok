package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("order")
public class OrderEntity {
    @TableId
    private Long id;
    private Long userId;
    private Long serviceId;
    private String address;
    private java.util.Date date;
    private String remark;
    private String status;
    private java.util.Date createdAt;
}