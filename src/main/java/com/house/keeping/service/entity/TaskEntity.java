package com.house.keeping.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 任务实体
 */
@Data
@TableName("`task`")
public class TaskEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务标题
     */
    private String title;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 任务类型：daily_task(日常任务)/special_task(特殊任务)/maintenance(维护任务)
     */
    private String type;
    
    /**
     * 任务状态：pending(待处理)/processing(进行中)/completed(已完成)/cancelled(已取消)/overdue(已过期)
     */
    private String status;
    
    /**
     * 优先级：low(低)/medium(中)/high(高)/urgent(紧急)
     */
    private String priority;
    
    /**
     * 任务分类
     */
    private String category;
    
    /**
     * 标签（JSON数组字符串）
     */
    private String tags;
    
    /**
     * 指派人ID
     */
    @TableField("assignee_id")
    private Long assigneeId;
    
    /**
     * 创建人ID
     */
    @TableField("creator_id")
    private Long creatorId;
    
    /**
     * 截止日期
     */
    @TableField("due_date")
    private Date dueDate;
    
    /**
     * 开始日期
     */
    @TableField("start_date")
    private Date startDate;
    
    /**
     * 完成日期
     */
    @TableField("completed_at")
    private Date completedAt;
    
    /**
     * 创建时间
     */
    @TableField("created_at")
    private Date createdAt;
    
    /**
     * 更新时间
     */
    @TableField("updated_at")
    private Date updatedAt;
    
    /**
     * 软删除标记
     */
    @TableField("is_deleted")
    private Boolean isDeleted;
}
