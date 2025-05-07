package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class UserEntity {
    @TableId
    private Long id;
    private String name;
    private String email;
    private Boolean isMember;
    private java.util.Date createdAt;
}