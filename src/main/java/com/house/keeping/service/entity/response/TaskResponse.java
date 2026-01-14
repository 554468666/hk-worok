package com.house.keeping.service.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 任务响应
 */
@Data
@Schema(description = "任务响应")
public class TaskResponse {
    
    @Schema(description = "任务ID")
    private Long id;
    
    @Schema(description = "任务标题")
    private String title;
    
    @Schema(description = "任务描述")
    private String description;
    
    @Schema(description = "任务类型")
    private String type;
    
    @Schema(description = "任务状态")
    private String status;
    
    @Schema(description = "优先级")
    private String priority;
    
    @Schema(description = "任务分类")
    private String category;
    
    @Schema(description = "标签")
    private List<String> tags;
    
    @Schema(description = "指派人信息")
    private UserInfoResponse assignee;
    
    @Schema(description = "创建人信息")
    private UserInfoResponse creator;
    
    @Schema(description = "截止日期")
    private String dueDate;
    
    @Schema(description = "开始日期")
    private String startDate;
    
    @Schema(description = "完成日期")
    private String completedAt;
    
    @Schema(description = "创建时间")
    private String createdAt;
    
    @Schema(description = "更新时间")
    private String updatedAt;
    
    @Schema(description = "评论列表")
    private List<Object> comments;
    
    @Schema(description = "附件列表")
    private List<Object> attachments;
}
