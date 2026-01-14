package com.house.keeping.service.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 更新任务请求
 */
@Data
@Schema(description = "更新任务请求")
public class UpdateTaskRequest {
    
    @Size(min = 1, max = 100, message = "任务标题长度必须在1-100个字符之间")
    @Schema(description = "任务标题", example = "更新后的任务标题")
    private String title;
    
    @Size(min = 1, max = 1000, message = "任务描述长度必须在1-1000个字符之间")
    @Schema(description = "任务描述", example = "更新后的任务描述")
    private String description;
    
    @Schema(description = "任务类型：daily_task/special_task/maintenance")
    private String type;
    
    @Schema(description = "优先级：low/medium/high/urgent")
    private String priority;
    
    @Schema(description = "指派人ID", example = "3")
    private Long assigneeId;
    
    @Schema(description = "任务状态：pending/processing/completed/cancelled")
    private String status;
    
    @Schema(description = "截止日期", example = "2026-01-10")
    private String dueDate;
    
    @Size(max = 20, message = "任务分类不能超过20个字符")
    @Schema(description = "任务分类")
    private String category;
    
    @Schema(description = "标签数组")
    private List<String> tags;
}
