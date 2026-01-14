package com.house.keeping.service.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建任务请求
 */
@Data
@Schema(description = "创建任务请求")
public class CreateTaskRequest {
    
    @NotBlank(message = "任务标题不能为空")
    @Size(min = 1, max = 100, message = "任务标题长度必须在1-100个字符之间")
    @Schema(description = "任务标题", example = "更新项目文档", required = true)
    private String title;
    
    @NotBlank(message = "任务描述不能为空")
    @Size(min = 1, max = 1000, message = "任务描述长度必须在1-1000个字符之间")
    @Schema(description = "任务描述", example = "根据最新要求更新项目相关文档，包括接口文档、用户手册等", required = true)
    private String description;
    
    @Schema(description = "任务类型：daily_task/special_task/maintenance", example = "daily_task")
    private String type;
    
    @Schema(description = "优先级：low/medium/high/urgent", example = "high")
    private String priority;
    
    @Schema(description = "指派人ID", example = "2")
    private Long assigneeId;
    
    @Schema(description = "截止日期", example = "2026-01-10")
    private String dueDate;
    
    @Size(max = 20, message = "任务分类不能超过20个字符")
    @Schema(description = "任务分类", example = "文档管理")
    private String category;
    
    @Schema(description = "标签数组", example = "[\"重要\", \"项目\"]")
    private List<String> tags;
}
