package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("member")
public class MemberEntity {
    @TableId
    private Long id;
    private Long userId;
    private Double remainingAmount;
    private Double rechargeAmount;
    private java.util.Date createdAt;
}