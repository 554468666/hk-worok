package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("employee")
public class EmployeeEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("work_years")
    private String workYears;

    private String identity;

    @TableField("is_team_leader")
    private Boolean isTeamLeader;

    @TableField("team_size")
    private String teamSize;

    private String resume;

    private Date createdAt;

    private Date updatedAt;

    @TableField("is_deleted")
    private Boolean isDeleted;
}
