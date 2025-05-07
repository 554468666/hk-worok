package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("evaluate")
public class EvaluateEntity {
    @TableId
    private Long id;
    private Long orderId;
    private Integer cleanliness;
    private Integer attitude;
    private String message;
    private java.util.Date createdAt;
}